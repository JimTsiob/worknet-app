package com.example.worknet.services;

import com.example.worknet.entities.Certification;

import java.util.List;

public interface CertificationService {
    List<Certification> getAllCertifications();
    Certification addCertification(Certification certification);
    Certification getCertificationById(Long id);
    Certification updateCertification(Long id, Certification certification);
    void deleteCertification(Long id);
}
