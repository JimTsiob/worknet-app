package com.syrtsiob.worknet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syrtsiob.worknet.model.SkillDTO;

public class SkillsFragment extends Fragment {

    Button addSkillButton;
    LinearLayout skillList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_skills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addSkillButton = requireView().findViewById(R.id.add_skills_button);
        addSkillButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditSkill.class);
            intent.putExtra(AddEditSkill.ACTIVITY_MODE, AddEditSkill.ADD_MODE);
            startActivity(intent);
        });

        skillList = requireView().findViewById(R.id.skills_list);

        AddSkillListEntry();
        AddSkillListEntry();
        AddSkillListEntry();
    }

    // TODO this is for testing purposes -- remove
    private void AddSkillListEntry() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View skillListEntry = inflater
                .inflate(R.layout.skills_entry_template, skillList, false);

        Button editButton = skillListEntry.findViewById(R.id.edit_skill_button);
        Button deleteButton = skillListEntry.findViewById(R.id.delete_skill_button);

        editButton.setOnClickListener(listener -> {
            // TODO implement functionality
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            skillList.removeView(skillListEntry);
        });

        skillList.addView(skillListEntry);
    }

    private void AddSkillListEntry(SkillDTO skillDTO) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View skillListEntry = inflater
                .inflate(R.layout.skills_entry_template, skillList, false);

        TextView name = skillListEntry.findViewById(R.id.skill_name);
        TextView privacy_label = skillListEntry.findViewById(R.id.privacy_label);

        name.setText(skillDTO.getName());

        // TODO update privacy label
        // privacy_label.setText(skillDTO.get());

        Button editButton = skillList.findViewById(R.id.edit_skill_button);
        Button deleteButton = skillList.findViewById(R.id.delete_skill_button);

        editButton.setOnClickListener(listener -> {
            Intent intent = new Intent(getActivity(), AddEditSkill.class);
            intent.putExtra(AddEditSkill.ACTIVITY_MODE, AddEditSkill.EDIT_MODE);
            intent.putExtra(AddEditSkill.SERIALIZABLE, skillDTO);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(listener -> {
            // TODO add call to database
            skillList.removeView(skillListEntry);
        });

        skillList.addView(skillListEntry);
    }
}