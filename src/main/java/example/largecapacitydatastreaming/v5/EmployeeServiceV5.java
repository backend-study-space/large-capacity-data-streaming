package example.largecapacitydatastreaming.v5;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v4.repository.EmployeeRepositoryV4;
import example.largecapacitydatastreaming.v4.service.EmployeeServiceV4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static example.largecapacitydatastreaming.support.FileWriteService.*;
import static example.largecapacitydatastreaming.support.FileWriteService.writeByThreads;

@Service
@TimeTracer
public class EmployeeServiceV5 {

    private final EmployeeRepositoryV4 employeeRepositoryV4;

    private final FileWriteService<EmployeeDto> fileWriteService;

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceV4.class);

    public EmployeeServiceV5(EmployeeRepositoryV4 employeeRepositoryV4, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV4 = employeeRepositoryV4;
        this.fileWriteService = fileWriteService;
    }

    public void writeFileByVirtualThreads(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);
        Queue<EmployeeDto> queue = new ConcurrentLinkedQueue<>();

        BufferedWriter bufferedWriter = createWriter(filePath);

        employeeRepositoryV4.resultSetStream(10000, (rs, rowNum) -> new Employee(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date")
                ), rowNum -> {
                    fileWriteService.writeBody(queue, bufferedWriter, writeByThreads());
//                    log.info("현재 {}개 처리되었습니다.", rowNum);
                })
                .map(EmployeeDto::create)
                .forEach(queue::add);
    }


}
