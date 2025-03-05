package com.SafetyNet.SafetyNetAlerts.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SafetyNet.SafetyNetAlerts.dto.MedicalRecordDTO;
import com.SafetyNet.SafetyNetAlerts.model.MedicalRecords;
import com.SafetyNet.SafetyNetAlerts.repository.InformationRepository;
import com.SafetyNet.SafetyNetAlerts.repository.MedicalRecordsRepository;

@Service
public class MedicalRecordService {

	@Autowired
	private InformationRepository informationRepository;
	@Autowired
	private MedicalRecordsRepository medicalRecordsRepository;

	public Object getAllMedicalRecords() throws IOException {
		return informationRepository.readMedicalRecords();
	}

	public void addMedicalRecord(MedicalRecordDTO medicalRecordDTO) throws IOException {
		MedicalRecords record = new MedicalRecords(medicalRecordDTO.getFirstName(), medicalRecordDTO.getLastName(),
				medicalRecordDTO.getBirthdate(), medicalRecordDTO.getMedications(), medicalRecordDTO.getAllergies());
		medicalRecordsRepository.addMedicalRecords(record);
	}

	public void updateMedicalRecord(String firstName, String lastName, MedicalRecordDTO medicalRecordDTO) throws IOException {
		medicalRecordsRepository.updateMedicalrecords(firstName, lastName, medicalRecordDTO);
	}

	public void deleteMedicalRecord(String firstName, String lastName) throws IOException {
		medicalRecordsRepository.deleteMedicalrecords(firstName, lastName);
	}
}
