package com.example.worknet.services;

import com.example.worknet.entities.Certification;
import com.example.worknet.repositories.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    private CertificationRepository certificationRepository;

    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    public Certification addCertification(Certification certification) {
        return certificationRepository.save(certification);
    }

    public Certification getCertificationById(Long id) {
        Optional<Certification> optionalCertification = certificationRepository.findById(id);
        return optionalCertification.orElse(null);
    }

    public Certification updateCertification(Long id, Certification certification) {
        if (certificationRepository.existsById(id)) {
            certification.setId(id); // Ensure the ID is set for update
            return certificationRepository.save(certification);
        }
        return null;
    }

    public void deleteCertification(Long id) {
        certificationRepository.deleteById(id);
    }
}
