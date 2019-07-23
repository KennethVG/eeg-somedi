package be.somedi.eeg.domain;

import be.somedi.eeg.entity.Person;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class XMLCreator {

	private static final String URI = "http://www.healthconnect.be/pemr/bindings";
	private static final String QUALIFIED_NAME = "HC_PEMR_Message";

	private final EEGReader eegReader;
	private Document document;

	private static final int SIZE_BIRTHDATE = 11;
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLCreator.class);

	@Value("${path-write}")
	private Path pathToWrite;
	@Value("${path-error}")
	private Path pathToError;

	public XMLCreator(EEGReader eegReader) {
		this.eegReader = eegReader;
	}

	private void writeToXml(Person patient) {
		LOGGER.info("Patiënt to write: " + patient);
		if (patient.getInss() != null) {
			LOGGER.info("Creatint XML...");
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result;
				if (patient.getInss().equalsIgnoreCase("No INSS")) {
					result = new StreamResult(new File(pathToError + "\\Patient_" + patient.getLastName() + "_" + patient.getInss() + ".xml"));
				} else {
					result = new StreamResult(new File(pathToWrite + "\\Patient_" + patient.getLastName() + "_" + patient.getInss() + ".xml"));
				}

				transformer.transform(source, result);

			} catch (TransformerException e) {
				try {
					LOGGER.error("Error creating XML", e);
					Files.write(pathToError, e.getMessage().getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void createNewXMLFiles() throws ParserConfigurationException {
		List<Person> patients = eegReader.getPatients();
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();

		patients.forEach(patient -> {
			if (patient == null) {
				return;
			}

			Message message = new Message();

			if (builder != null) {
				document = builder.newDocument();
			}

			Element rootEl = document.createElementNS(URI, QUALIFIED_NAME);
			document.appendChild(rootEl);

			Element version = document.createElement("Version");
			version.appendChild(document.createTextNode("1"));
			rootEl.appendChild(version);

			// MESSAGE HEADER
			Element msgHeader = document.createElement("MsgHeader");
			rootEl.appendChild(msgHeader);
			Element msgDateTime = document.createElement("MsgDateTime");
			msgDateTime.appendChild(document.createTextNode(message.getMessageDateTime()));
			msgHeader.appendChild(msgDateTime);
			Element msgId = document.createElement("MsgID");
			msgId.appendChild(document.createTextNode(message.getMessageID()));
			msgHeader.appendChild(msgId);
			Element archiveType = document.createElement("ArchiveType");
			archiveType.appendChild(document.createTextNode("20"));
			msgHeader.appendChild(archiveType);
			Element action = document.createElement("Action");
			action.appendChild(document.createTextNode("InsertUpdate"));
			msgHeader.appendChild(action);

			// MESSAGE BODY
			Element msgBody = document.createElement("MsgBody");
			rootEl.appendChild(msgBody);

			Element from = document.createElement("From");
			msgBody.appendChild(from);
			Element company = document.createElement("CompanyName");
			company.appendChild(document.createTextNode("Somedi Digitaal Archief"));
			from.appendChild(company);

			Element pat = document.createElement("Patient");
			msgBody.appendChild(pat);
			Element patientID = document.createElement("PatientID");
			patientID.appendChild(document.createTextNode(patient.getInss()));
			pat.appendChild(patientID);
			Element INSS = document.createElement("INSS");
			if (patient.getInss() != null) {
				INSS.appendChild(document.createTextNode(patient.getInss()));
			} else {
				patient.setInss("No INSS");
				INSS.appendChild(document.createTextNode(patient.getInss()));
			}
			pat.appendChild(INSS);
			Element name = document.createElement("Name");
			name.appendChild(document.createTextNode(patient.getLastName()));
			pat.appendChild(name);
			Element firstname = document.createElement("FirstName");
			firstname.appendChild(document.createTextNode(patient.getFirstName()));
			pat.appendChild(firstname);
			Element dateOfBirth = document.createElement("DateOfBirth");
			dateOfBirth.appendChild(document.createTextNode(StringUtils.left(patient.getBirthDate().toString(), SIZE_BIRTHDATE)));
			pat.appendChild(dateOfBirth);

			Element dir = document.createElement("Direction");
			dir.appendChild(document.createTextNode("IN"));
			msgBody.appendChild(dir);

			Element actionDateTime = document.createElement("ActionDateTime");
			actionDateTime.appendChild(document.createTextNode(message.getActionDateTime()));
			msgBody.appendChild(actionDateTime);

			Element subject = document.createElement("Subject");
			subject.appendChild(document.createTextNode("EEG"));
			msgBody.appendChild(subject);

			Element letter = document.createElement("Letter");
			msgBody.appendChild(letter);
			Element receiver = document.createElement("Receiver");
			letter.appendChild(receiver);
			Element INSSr = document.createElement("INSS");
			// INSS van Patrick Vermylen
			INSSr.appendChild(document.createTextNode("71060921974"));
			receiver.appendChild(INSSr);
			Element link = document.createElement("Link");
			link.appendChild(document.createTextNode(patient.getUrlToPDF().replaceAll("\n", "")));
			letter.appendChild(link);

			// Write the content into xml file
			writeToXml(patient);
		});
	}
}
