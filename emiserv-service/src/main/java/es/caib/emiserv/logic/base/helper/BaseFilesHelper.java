package es.caib.emiserv.logic.base.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Mètodes per a gestionar arxius a dins la carpeta files de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseFilesHelper {

	/**
	 * Desa un fitxer donat el seu contingut en un forma d'array de bytes.
	 *
	 * @param folder
	 *            la carpeta pel nou fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @param content
	 *            el contingut del fitxer.
	 * @throws IOException si hi ha algun problema desant el fitxer.
	 */
	public void save(
			String folder,
			String name,
			byte[] content) throws IOException {
		try (FileOutputStream fos = getSaveFileOutputStream(folder, name)) {
			fos.write(content);
		}
	}

	/**
	 * Desa un fitxer donat el seu contingut en forma de ByteArrayOutputStream.
	 *
	 * @param folder
	 *            la carpeta pel nou fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @param content
	 *            el contingut del fitxer.
	 * @throws IOException si hi ha algun problema desant el fitxer.
	 */
	public void save(
			String folder,
			String name,
			ByteArrayOutputStream content) throws IOException {
		try (FileOutputStream fos = getSaveFileOutputStream(folder, name)) {
			content.writeTo(fos);
		}
	}

	/**
	 * Obté un FileOutputStream per a desar un fitxer.
	 *
	 * @param folder
	 *            la carpeta pel nou fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @return el FileOutputStream.
	 * @throws IOException si hi ha algun problema generant el FileOutputStream.
	 */
	public FileOutputStream getSaveFileOutputStream(
			 String folder,
			String name) throws IOException {
		File fitxer = new File(newFolderFile(folder, true), sanitizeName(name));
		createParentIfNotExists(fitxer);
		return new FileOutputStream(fitxer);
	}

	/**
	 * Llegeix el contingut d'un fitxer.
	 *
	 * @param folder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @return el contingut del fitxer com un array de bytes.
	 * @throws IOException si hi ha algun problema llegint el fitxer.
	 */
	public byte[] read(
			String folder,
			String name) throws IOException {
		File fitxer = new File(newFolderFile(folder, false), sanitizeName(name));
		try (FileInputStream fis = new FileInputStream(fitxer)) {
			return fis.readAllBytes();
		}
	}

	/**
	 * Esborra el fitxer especificat.
	 *
	 * @param folder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @throws IOException si hi ha algun problema esborrant el fitxer.
	 */
	public void delete(
			String folder,
			String name) throws IOException {
		File fitxer = new File(newFolderFile(folder, false), sanitizeName(name));
		Files.delete(fitxer.toPath());
	}

	/**
	 * Comprova si el fitxer especificat existeix.
	 *
	 * @param folder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param name
	 *            el nom del fitxer.
	 * @return true si existeix o false en cas contrari.
	 */
	public boolean exists(
			String folder,
			String name) {
		File fitxer = new File(newFolderFile(folder, false), sanitizeName(name));
		return fitxer.exists();
	}

	/**
	 * Copia un fitxer.
	 *
	 * @param sourceFolder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param sourceName
	 *            el nom del fitxer existent.
	 * @param targetFolder
	 *            la carpeta a on es vol moure el fitxer (pot ser null).
	 * @param targetName
	 *            el nom del nou fitxer.
	 * @param replace
	 *            indica si s'ha de sobreescriure el fitxer destí.
	 * @throws IOException si hi ha algun problema movent el fitxer.
	 */
	public void copy(
			String sourceFolder,
			String sourceName,
			String targetFolder,
			String targetName,
			boolean replace) throws IOException {
		File source = new File(newFolderFile(sourceFolder, false), sanitizeName(sourceName));
		File target = new File(newFolderFile(targetFolder, false), sanitizeName(targetName));
		CopyOption[] options = replace ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : null;
		Files.copy(
				source.toPath(),
				target.toPath(),
				options);
	}

	/**
	 * Mou un fitxer.
	 *
	 * @param sourceFolder
	 *            la carpeta a on es troba fitxer (pot ser null).
	 * @param sourceName
	 *            el nom del fitxer existent.
	 * @param targetFolder
	 *            la carpeta a on es vol moure el fitxer (pot ser null).
	 * @param targetName
	 *            el nom del nou fitxer.
	 * @param replace
	 *            indica si s'ha de sobreescriure el fitxer destí.
	 * @throws IOException si hi ha algun problema movent el fitxer.
	 */
	public void move(
			String sourceFolder,
			String sourceName,
			String targetFolder,
			String targetName,
			boolean replace) throws IOException {
		File source = new File(newFolderFile(sourceFolder, false), sanitizeName(sourceName));
		File target = new File(newFolderFile(targetFolder, false), sanitizeName(targetName));
		CopyOption[] options = replace ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : null;
		Files.move(
				source.toPath(),
				target.toPath(),
				options);
	}

	/**
	 * Indica si la funcionalitat està activa (si el mètode getFilesPath retorna alguna cosa).
	 *
	 * @return true si està activa o false en cas contrari.
	 */
	public boolean isFilesActive() {
		String files = getFilesPath();
		return files != null && !files.isEmpty();
	}

	protected File newFolderFile(String folder, boolean createIfNotExists) {
		String files = getFilesPath();
		String path;
		if (folder != null) {
			String sanitizedFolder = sanitizeName(folder);
			if (files.endsWith("/")) {
				path = files + sanitizedFolder;
			} else {
				path = files + "/" + sanitizedFolder;
			}
		} else {
			path = files;
		}
		File target = new File(path);
		if (createIfNotExists) target.mkdirs();
		return target;
	}

	/**
	 * Substitueix el caràcter "/" per "_" en un nom de fitxer o de carpeta, per evitar que
	 * s'interpreti com un separador de camins i es puguin crear o accedir fitxers fora de la
	 * carpeta esperada.
	 *
	 * @param name
	 *            el nom del fitxer o de la carpeta.
	 * @return el nom sanejat, o null si el nom especificat és null.
	 */
	protected String sanitizeName(String name) {
		return name != null ? name.replace("/", "_") : null;
	}

	protected void createParentIfNotExists(File file) {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			if (!file.getParentFile().mkdirs()) {
				log.error("Couldn't create folder {}", parentFile);
			}
		}
	}

	protected abstract String getFilesPath();

}
