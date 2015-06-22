package com.synpatix.toast.runtime.core.runtime;
//package com.synaptix.redpepper.backend.core.runtime;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import org.apache.commons.collections.CollectionUtils;
//
//
//public class RuntimeUtils {
//	public static void sendMail(File zipFile) {
//		// Send files by email
//		JavaxMailFactory mailFactory = new JavaxMailFactory("vip-geflanmail.inetgefco.net", 25, "nobody", new String("password"), false);
//		List<String> recipientsList = new ArrayList<String>();
//		recipientsList.add("nicolas.sauvage@gefco.fr");
//		recipientsList.add("sallah.kokaina@gefco.fr");
//
//		Attachment attachment = new Attachment("report.zip", "application/zip", zipFile);
//		Attachment attachmentList[] = { attachment };
//		try {
//			mailFactory.sendMail("nicolas.sauvage@gefco.fr", recipientsList.toArray(new String[recipientsList.size()]), null, null, "title", "text", attachmentList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public class ZipFile {
//
//		List<String> fileList;
//
//		ZipFile() {
//			fileList = new ArrayList<String>();
//		}
//
//		public void zipIt(String zipFile) {
//			if (CollectionUtils.isEmpty(this.fileList)) {
//				return;
//			}
//
//			byte[] buffer = new byte[1024];
//			String source = "";
//			try {
//				try {
//					source = testReportFolder.substring(testReportFolder.lastIndexOf("\\") + 1, testReportFolder.length());
//				} catch (Exception e) {
//					source = testReportFolder;
//				}
//				FileOutputStream fos = new FileOutputStream(zipFile);
//				ZipOutputStream zos = new ZipOutputStream(fos);
//
//				System.out.println("Output to Zip : " + zipFile);
//
//				for (String file : this.fileList) {
//
//					System.out.println("File Added : " + file);
//					ZipEntry ze = new ZipEntry(source + File.separator + file);
//					zos.putNextEntry(ze);
//
//					FileInputStream in = new FileInputStream(testReportFolder + File.separator + file);
//
//					int len;
//					while ((len = in.read(buffer)) > 0) {
//						zos.write(buffer, 0, len);
//					}
//
//					in.close();
//				}
//
//				zos.closeEntry();
//				// remember close it
//				zos.close();
//
//				System.out.println("Folder successfully compressed");
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//
//		public void generateFileList(File node) {
//
//			// add file only
//			if (node.isFile()) {
//				fileList.add(generateZipEntry(node.toString()));
//
//			}
//
//			if (node.isDirectory()) {
//				String[] subNote = node.list();
//				for (String filename : subNote) {
//					generateFileList(new File(node, filename));
//				}
//			}
//
//		}
//
//		private String generateZipEntry(String file) {
//			return file.substring(testReportFolder.length(), file.length());
//		}
//	}
//	
//	public File zipFiles() {
//		// Zip files
//		ZipFile appZip = new ZipFile();
//		appZip.generateFileList(new File(testReportFolder));
//		String OUTPUT_ZIP_FILE = testReportFolder + "testReport.zip";
//		appZip.zipIt(OUTPUT_ZIP_FILE);
//		return new File(OUTPUT_ZIP_FILE);
//	}
//}
