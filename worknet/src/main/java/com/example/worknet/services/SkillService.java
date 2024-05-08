package com.example.worknet.services;

import com.example.worknet.entities.Skill;

import java.util.List;

public interface SkillService {
    Skill getSkillById(Long id);
    List<Skill> getAllSkills();
    Skill addSkill(Skill skill);
    Skill updateSkill(Long id,Skill skill);
    void deleteSkill(Long id);
}
