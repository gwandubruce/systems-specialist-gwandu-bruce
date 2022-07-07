
package co.nmb.systemsdevelopmentspecialist.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.nmb.systemsdevelopmentspecialist.models.Employee;
import co.nmb.systemsdevelopmentspecialist.models.SalaryStatus;
import co.nmb.systemsdevelopmentspecialist.models.UploadFileResponse;
import co.nmb.systemsdevelopmentspecialist.repositories.EmployeeRepository;
import co.nmb.systemsdevelopmentspecialist.services.EmployeeService;
import co.nmb.systemsdevelopmentspecialist.services.EmployeeStatusResponseService;
import co.nmb.systemsdevelopmentspecialist.services.FileStorageService;
import co.nmb.systemsdevelopmentspecialist.services.FileUploadService;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class FileController {

    private Map<String, Map<String, String>> status;

    private String message, button;

    EmployeeStatusResponseService theStats = new EmployeeStatusResponseService();

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;


    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    //vas
    @PostMapping("/uploadFileRedu")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, String paymentCategory) {

        FileUploadService.readFile(file, paymentCategory);

        return new UploadFileResponse(file.getName(), paymentCategory,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/login")
    public String logindirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/home";

    }


    @GetMapping("/userStatus")
    public String checkUserStatusIndex() {
        return "userStatus";
    }

    @GetMapping("/updateSubscriber")
    public String updateEmployeeIndex() {


        return "updateSubscriber";
    }

    @GetMapping("/")
    public String login() {
        return this.logindirect();
    }

    @GetMapping("/home")
    public String home() {
        return "uploadFileFrontEnd";
    }

    @PostMapping("/uploadFileFrontEnd")
    public String uploadFileFrontEnd(@RequestParam("file") MultipartFile file, @RequestParam("search_categories") String paymentCategory,
                                     RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatus";
        }

        status = FileUploadService.readFile(file, paymentCategory);

        if (status.containsValue(null)) {
            button = "Retry again";
            message = "Error occured on the first Employee record. Please contact the system admin or try again later.";
            return "redirect:/uploadMessage";
        } else if (status.isEmpty()) {
            button = "Upload another file";
            message = "All employees were successfully paid their salaries.";
            return "redirect:/uploadMessage";
        }

        redirectAttributes.addFlashAttribute("message",
                status);

        return "redirect:/uploadStatus";

    }

    @GetMapping("/uploadMessage")
    public String uploadSuccess(Model model) {
//        message != null && button != null
        if (message != null && button != null) {
            model.addAttribute("success", !status.containsValue(null));
            model.addAttribute("message", message);
            model.addAttribute("button", button);
            return "upload";
        }

        return "redirect:/home";
    }

    @GetMapping("/statusResult")
    public String userStatus(Model model) {
        model.addAttribute("stats", theStats);
        return "statusResult";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus(Model model) {
        model.addAttribute("results", status);
        return "uploadStatus";
    }


    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
// Endpoint for adding Individual Employee .........................................................................

    @RequestMapping("/add_user")
    @ResponseBody
    public Employee addEmployee(@RequestBody Employee employee) {

        if (employeeRepository.findByAccountNumber(employee.getAccountNumber()) != null) {

            return null;
        }

        employee.setSalaryStatus(SalaryStatus.PENDING);

        return employeeService.addEmployee(employee);

    }


    @RequestMapping("/salary/approve/{id}")
    public String approveEmployee(@PathVariable("id") Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);


        if (!employee.isPresent()) {

            return null;
        }

        Employee employee1 = employee.get();

        employee1.setSalaryStatus(SalaryStatus.APPROVED);

        employeeService.updateEmployee(employee1);

        return "redirect:/Employees";

    }

    @RequestMapping("/salary/reject/{id}")
    public String rejectEmployee(@PathVariable("id") Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);


        if (!employee.isPresent()) {

            return null;
        }

        Employee employee1 = employee.get();

        employee1.setSalaryStatus(SalaryStatus.BLOCKED);

        employeeService.updateEmployee(employee1);

        return "redirect:/Employees";

    }
// Endpoints for updating employee .........................................................................

    @PostMapping("/update_user")
    public String updateCustomer(Employee employee) {

        if (employeeRepository.findByAccountNumber(employee.getAccountNumber()) == null) {

            return "updateError";
        }

        Employee employee1 = employeeRepository.findByAccountNumber(employee.getAccountNumber()).orElse(new Employee());
        employee1.setSalaryStatus(employee.getSalaryStatus());
        employee1.setSalary(employee.getSalary());
        employee1.setFullName(employee.getFullName());
        employee1.setDatePaid(LocalDate.now());

        return "updateSuccess";

    }


    // Displaying all employees and pagination
    String stat = "";

    @GetMapping("/Employees")
    public String home(Model m) {
        return display(1, m);

    }


    @GetMapping("/page/{pageNo}")
    public String display(@PathVariable(value = "pageNo") int pageNo, Model m) {

        int pageSize = 5;   // How many records on per page
        Page<Employee> page = employeeService.findByPagination(pageNo, pageSize);
        List<Employee> list = page.getContent();
        m.addAttribute("currentPage", pageNo);
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalRecords", page.getTotalElements());
        m.addAttribute("list", list);
        return "/Employees";

    }


    // show edit subscriber page and form
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditEmployeeForm(@PathVariable(name = "id") Long id) {

        Long identity = Long.valueOf(id);
        ModelAndView mav = new ModelAndView("updateSubscribers");

        Employee employee = employeeService.getEmployee(identity);
        mav.addObject("doctor", employee);

        return mav;
    }


    @RequestMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable(name = "id") Long id) {
        Long identity = Long.valueOf(id);
        employeeService.deleteEmployee(identity);

        return "redirect:/";
    }


    @PutMapping("/update_user/{id}")
    public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee, @PathVariable long id) {

        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent())
            return ResponseEntity.notFound().build();

        employee.setSalaryStatus(employee.getSalaryStatus());

        employeeRepository.save(employee);

        return ResponseEntity.noContent().build();
    }


    // Search employee by Account Number

    @GetMapping("/searchSubscriber")
    public String add(Model model) {
        List<Employee> employees = employeeService.listAllEmployees();
        model.addAttribute("subscriber", new Employee());
        return "/searchSubscriber";
    }


    @PostMapping("/search")
    public String doSearchEmployee(@ModelAttribute("employeeSearchFormData") Employee formData, Model model) {
        employeeService.findByAccountNumber(formData.getAccountNumber())
                .ifPresent(emp -> model.addAttribute("subscriber", emp));

        return "/searchSubscriber";
    }


}
