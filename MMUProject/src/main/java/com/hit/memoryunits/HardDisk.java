package com.hit.memoryunits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.hit.util.MMULogger;

public class HardDisk {

	private static HardDisk m_HardDisk = null;
	private static final int m_Size = 1000;
	private static String m_DEFAULT_FILE_NAME = "./resources/HardDiskData.txt";
	Map<Long, Page<byte[]>> m_HardDiskData;

	private HardDisk() throws FileNotFoundException, ClassNotFoundException, IOException {
		m_HardDiskData = new HashMap<Long, Page<byte[]>>(m_Size);
		File HardDiskFile = new File(m_DEFAULT_FILE_NAME);

		if (HardDiskFile.exists()) {
			readDataFromHd();
		} else {
			HardDiskFile.createNewFile();
			initializeHD();
			writeDataToHd();
		}
	}

	public static HardDisk getInstance() throws FileNotFoundException, ClassNotFoundException, IOException {
		if (m_HardDisk == null) {
			m_HardDisk = new HardDisk();
		}

		return m_HardDisk;
	}

	// This method is called when a page is not in fast memory (RAM)
	public Page<byte[]> pageFault(Long pageId)
			throws java.io.FileNotFoundException, java.io.IOException, ClassNotFoundException {
		MMULogger logger = MMULogger.getInstance();
		logger.write(MessageFormat.format("PF:{0}{1}", pageId.toString(), System.lineSeparator()), Level.INFO);
		readDataFromHd();
		Page<byte[]> pageGivenIdReturn = m_HardDiskData.get(pageId);

		return pageGivenIdReturn;
	}

	// This method is called when a page is not in fast memory (RAM) and RAM is
	// also with full capacity
	public Page<byte[]> pageReplacement(Page<byte[]> moveToHdPage, Long moveToRamId)
			throws java.io.FileNotFoundException, java.io.IOException, ClassNotFoundException {
		MMULogger logger = MMULogger.getInstance();
		logger.write(MessageFormat.format("PR:MTH {0} MTR {1}{2}", moveToHdPage.getM_Id().toString(),
				moveToRamId.toString(), System.lineSeparator()), Level.INFO);

		m_HardDiskData.clear();
		readDataFromHd();
		m_HardDiskData.put(moveToHdPage.getM_Id(), moveToHdPage);
		writeDataToHd();

		return m_HardDiskData.get(moveToRamId);
	}

	@SuppressWarnings("unchecked")
	private void readDataFromHd() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream hdInputStream = new ObjectInputStream(new FileInputStream(m_DEFAULT_FILE_NAME));

		try {
			m_HardDiskData = (HashMap<Long, Page<byte[]>>) hdInputStream.readObject();
		} catch (ClassNotFoundException CNFE) {
			MMULogger.getInstance().write(CNFE.getMessage(), Level.SEVERE);
		} finally {
			hdInputStream.close();
		}
	}

	private void writeDataToHd() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectOutputStream hdOutputStream = new ObjectOutputStream(new FileOutputStream(m_DEFAULT_FILE_NAME));

		try {
			hdOutputStream.writeObject(m_HardDiskData);
		} catch (FileNotFoundException FNFE) {
			MMULogger.getInstance().write(FNFE.getMessage(), Level.SEVERE);
		} finally {
			hdOutputStream.flush();
			hdOutputStream.close();
		}
	}

	private void initializeHD() {
		byte[] pageByteData = null;
		for (Integer i = 0; i < m_Size; i++) {
			String iString = i.toString();
			pageByteData = iString.getBytes();
			m_HardDiskData.put((long) i, new Page<>((long) i, pageByteData));
		}
	}
}
