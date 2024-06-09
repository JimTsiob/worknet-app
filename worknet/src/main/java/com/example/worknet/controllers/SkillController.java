package com.example.worknet.controllers;

import com.example.worknet.dto.SkillDTO;
import com.example.worknet.entities.Education;
import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.services.SkillService;
import com.example.worknet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private UserService userService;

    private final StrictModelMapper modelMapper = new StrictModelMapper();


    @GetMapping("/")
    public ResponseEntity<?> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();

        List<SkillDTO> skillDTOList =  skills.stream()
                .map(skill -> modelMapper.map(skill, SkillDTO.class))
                .toList();

        return ResponseEntity.ok(skillDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSkillById(@PathVariable Long id) {
        Skill skill = skillService.getSkillById(id);

        SkillDTO skillDTO =  modelMapper.map(skill, SkillDTO.class);
        if (skillDTO != null){
            return ResponseEntity.ok(skillDTO);
        }else{
            String errorMessage = "Skill with ID " + id + " not found.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addSkill(@RequestBody SkillDTO skillDTO, @RequestParam String email) {
        try {
            Skill skill = modelMapper.map(skillDTO, Skill.class);
            User user = userService.getUserByEmail(email);
            List<Skill> skills = user.getSkills();

            // do not allow same education to be added twice
            for (Skill s: skills) {
                if (skillService.equalsSkill(skill,s)){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot add same skill twice.");
                }
            }

            skillService.addSkill(skill, user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Skill added successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to add skill: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSkill(@PathVariable Long id, @RequestBody SkillDTO skillDTO) {
        try {

            Skill existingSkill = skillService.getSkillById(id);
            if (existingSkill == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Skill with ID " + id + " does not exist.");
            }

            modelMapper.map(skillDTO, existingSkill);

            skillService.updateSkill(id, existingSkill);

            return ResponseEntity.ok("Skill updated successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to update skill with id: " + id + " / error message: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        try {

            Skill existingSkill = skillService.getSkillById(id);
            if (existingSkill == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Skill with ID " + id + " does not exist.");
            }

            skillService.deleteSkill(id);

            return ResponseEntity.ok("Skill deleted successfully");
        } catch (Exception e) {
            String errorMessage = "Failed to delete skill: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
