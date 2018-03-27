package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.lang.Integer;

import hello.Patient;
import hello.PatientRepository;
import hello.LabTest;
import hello.LabTestRepository;


@Controller    // This means that this class is a Controller
// @RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
	@Autowired // This means to get the bean called userRepository
	           // Which is auto-generated by Spring, we will use it to handle the data
	private PatientRepository patientRepository;
	@Autowired 
	private LabTestRepository labTestRepository;

	@GetMapping("/home")
    public String home(Model model) {
    	List<Patient> patients = new ArrayList<>();
	    patientRepository.findAll().forEach(patients::add);
	    model.addAttribute("patients", patients);

        return "home";
    }

    @GetMapping("/search")
    public String search(Model model) {
        return "search";
    }

    @GetMapping("/individual")
    public String individual(Model model, @RequestParam Long pid
    	, @RequestParam String sdate, @RequestParam String edate) {
    	try{
	    	Patient p = patientRepository.findById(pid).get();
	    	model.addAttribute("patient", p);

	    	Date start = new Date();
	    	Date end = new Date();

	    	try {
		    	start = new SimpleDateFormat("yyyy-MM-dd").parse(sdate);
		    	end = new SimpleDateFormat("yyyy-MM-dd").parse(edate);
		    } catch (Exception e) {

			}
	    	model.addAttribute("labTests", getLabTestsByPidWithDate(pid, start, end));
	    } catch (Exception e) {

	    }
        return "individual";
    }

    // @GetMapping("/population")
    // public String population(Model model) {
    //     return "population";
    // }

    @GetMapping("/mkck")
    public String mkck(Model model) {
	    model.addAttribute("patients", getCategoryPatients("MKCK"));
        return "mkck";
    }

    @GetMapping("/pd")
    public String pd(Model model) {
	    model.addAttribute("patients", getCategoryPatients("PD"));
        return "pd";
    }

    @GetMapping("/hd")
    public String hd(Model model) {
    	model.addAttribute("patients", getCategoryPatients("HD"));
        return "hd";
    }

    @GetMapping("/ahd")
    public String ahd(Model model) {
    	model.addAttribute("patients", getCategoryPatients("AHD"));
        return "ahd";
    }

    @GetMapping("/work")
    public String work(Model model) {
        return "work";
    }

	@GetMapping("/patients")
    public @ResponseBody Iterable<Patient> getAllPatients() {
		return patientRepository.findAll();
	}

	@GetMapping("/labTest")
    public @ResponseBody Iterable<LabTest> getAllLabTests() {
		return labTestRepository.findAll();
	}

	public List<Patient> getCategoryPatients(String category) {
		List<Patient> patients = new ArrayList<>();
    	Iterable<Patient> iterator = patientRepository.findAll();
	    for(Patient p: iterator) {
	    	if (p.getCategory().equals(category)) {
	    		patients.add(p);
	    	}
	    }
	    return patients;
	}

	public List<LabTest> getLabTestsByPidWithDate(Long id, Date sdate, Date edate) {
		List<LabTest> labTests = new ArrayList<>();
    	Iterable<LabTest> iterator = labTestRepository.findAll();
	    for(LabTest e: iterator) {
	    	if (e.getPid() == id && e.getTestDate().after(sdate) && e.getTestDate().before(edate)) {
	    		labTests.add(e);
	    	}
	    }
	    return labTests;
	}

	// @GetMapping("/patients/{id}")
	// public Patient getPatientById(@PathVariable(value = "id") Long patientId) {
	//     return patientRepository.findById(patientId);
	// }

	@GetMapping(path="/add") // Map ONLY GET Requests
	public @ResponseBody String addNewPatient (@RequestParam String name
			, @RequestParam String gender, @RequestParam Integer age
			, @RequestParam Double egfr, @RequestParam Integer date
			, @RequestParam Double rate, @RequestParam String category
			, @RequestParam String smoking, @RequestParam String cancer) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		Patient n = new Patient();
		n.setName(name);
		n.setGender(gender);
		n.setAge(age);
		n.setEgfr(egfr);

		Date testDate = new Date();
		try {
			testDate = new SimpleDateFormat("yyyyMMdd").parse(Integer.toString(date));
		} catch (Exception e) {

		}
		n.setCategory(category);
		n.setTestDate(testDate);
		n.setRate(rate);
		n.setSmoking(smoking);
		n.setCancer(cancer);
		patientRepository.save(n);
		return "Saved";
	}

	@GetMapping(path="/lab") // Map ONLY GET Requests
	public @ResponseBody String addNewLabTest (@RequestParam Long pid
			, @RequestParam String labName, @RequestParam String labUnit
			, @RequestParam Double labValue, @RequestParam Integer date) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		LabTest n = new LabTest();
		n.setPid(pid);
		n.setLabValue(labValue);
		n.setLabName(labName);
		n.setLabUnit(labUnit);

		Date testDate = new Date();
		try {
			testDate = new SimpleDateFormat("yyyyMMdd").parse(Integer.toString(date));
		} catch (Exception e) {

		}
		n.setTestDate(testDate);
		labTestRepository.save(n);
		return "Saved";
	}

	// @GetMapping(path="/all")
	// public @ResponseBody Iterable<User> getAllUsers() {
	// 	// This returns a JSON or XML with the users
	// 	return userRepository.findAll();
	// }
}