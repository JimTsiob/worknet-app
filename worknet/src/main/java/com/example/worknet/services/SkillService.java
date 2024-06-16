package com.example.worknet.services;

import com.example.worknet.entities.Skill;
import com.example.worknet.entities.User;

import java.util.List;

public interface SkillService {
    Skill getSkillById(Long id);
    List<Skill> getAllSkills();
    Skill addSkill(Skill skill, User user);
    Skill updateSkill(Long id,Skill skill);
    void deleteSkill(Long id);
    boolean equalsSkill(Skill skill1, Skill skill2);
}
