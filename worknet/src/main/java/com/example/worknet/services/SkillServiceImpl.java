package com.example.worknet.services;

import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;
import com.example.worknet.modelMapper.StrictModelMapper;
import com.example.worknet.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public Skill getSkillById(Long id) {
        Optional<Skill> skill = skillRepository.findById(id);

        return skill.orElse(null);
    }

    public List<Skill> getAllSkills(){
        return skillRepository.findAll();
    }

    public Skill addSkill(Skill skill, User user){
        user.getSkills().add(skill);

        return skillRepository.save(skill);
    }

    public Skill updateSkill(Long id, Skill skill) {
        Optional<Skill> skillOptional = skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            Skill existingSkill = skillOptional.get();

            StrictModelMapper modelMapper = new StrictModelMapper();

            modelMapper.map(skill, existingSkill);

            return skillRepository.save(existingSkill);
        }

        return null;
    }

    public void deleteSkill(Long id){
        skillRepository.deleteById(id);
    }

    public boolean equalsSkill(Skill skill1, Skill skill2) {
        if (skill1.getName().trim().equalsIgnoreCase(skill2.getName().trim()) && 
            skill1.getUser().getId() == skill2.getUser().getId()) {
            return true;
        }

        return false;
    }
}
