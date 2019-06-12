package student.pact.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class StudentProvider {

    private String backendURL = "";


    public void setBackendURL(String URLBase, boolean get) {
        if(get) {
            this.backendURL = URLBase + "/students/getStudents";
        }else{
            this.backendURL = URLBase + "/students/addStudent";
        }
    }

    public Student getStudentFromProvider() {
        RestTemplate restTemplate = new RestTemplate();
        Student response = restTemplate.getForObject(backendURL, Student.class);
        return response;
    }

    public List<Student> addStudentFromProvider(Map<String, Object> student) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Student>> response = restTemplate.postForObject(backendURL, student, ResponseEntity.class);
        List<Student> students = response.getBody();
        return students;
    }
}
