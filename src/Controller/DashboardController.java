package Controller;

import Service.StudentService;
import Service.RoomService;
import Service.ContractService;
import Service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private FeeService feeService;
