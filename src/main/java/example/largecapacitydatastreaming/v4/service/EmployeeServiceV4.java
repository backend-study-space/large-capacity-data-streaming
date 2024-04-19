package example.largecapacitydatastreaming.v4.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.SerializableCustom;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v4.repository.EmployeeRepositoryV4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@TimeTracer
public class EmployeeServiceV4 {

    private final EmployeeRepositoryV4 employeeRepositoryV4;

    private final FileWriteService<EmployeeDto> fileWriteService;

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceV4.class);

    public EmployeeServiceV4(EmployeeRepositoryV4 employeeRepositoryV4, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV4 = employeeRepositoryV4;
        this.fileWriteService = fileWriteService;
    }

    public void writeFileByInterface(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        employeeRepositoryV4.resultSetStream((rs, rowNum) -> new Employee(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date")
                ), rowNum -> log.info("현재 {}개 처리되었습니다.", rowNum))
                .map(EmployeeDto::create)
                .forEach(employeeDto -> fileWriteService.writeBody(employeeDto, bufferedWriter));
    }

    public void writeFileByMultiVirtualThreads(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        Queue<EmployeeDto> queue = new ConcurrentLinkedQueue<>();

        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filePath, true), 65536);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        employeeRepositoryV4.resultSetStream(1000, (rs, rowNum) -> new Employee(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("salary"),
                rs.getDate("hire_date")
        ), rowNum -> { // Consumer -> Supplier로 바꾸기.
            Thread.ofVirtual().start(() -> {
                fileWriteService.writeBody(queue, bufferedWriter);
                log.info("현재 {}개 처리되었습니다.", rowNum);
            });
        })
                .map(EmployeeDto::create)
                .forEach(queue::add);
    }
}
