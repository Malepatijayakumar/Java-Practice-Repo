package com.practice.jdbc.session1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

/**
 * @author JaiKumar
 *
 */
public class Jdbc11ReadBlobFile {
	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			connection = JdbcUtil.getConnection();
			if (connection == null) {
				System.out.println("no connection");
				System.exit(0);
			}
			String query = "SELECT first_name,photo FROM `student` WHERE roll_number =?";
			pstmt = connection.prepareStatement(query);
			readAndSaveFile(pstmt, resultSet);
		} catch (SQLException | IOException e) {
			System.out.println("Exception :: " + e);
		} finally {
			try {
				JdbcUtil.cleanUp(connection, pstmt, resultSet);
			} catch (SQLException e) {
				System.out.println("Exception occured while closing resources");
			}
		}
	}

	public static void readAndSaveFile(PreparedStatement pstmt, ResultSet resultSet) throws SQLException, IOException {
		System.out.println("Enter uid for fetching Image");
		Long rollNumber = scan.nextLong();
		pstmt.setLong(1, rollNumber);
		resultSet = pstmt.executeQuery();
		while (resultSet.next()) {
			String firstName = resultSet.getString(1);
			InputStream in = resultSet.getBinaryStream(2);
			FileOutputStream fos = new FileOutputStream(new File(firstName + ".jpg"));

			// Reading 1 by 1 byte of data. which impacts performance
//			int data = in.read();
//			while (data != -1) {
//				fos.write(data);
//				data = in.read();
//			}
			// Read specific number of bytes of data at once, and write that data into
			// file.And read again if something is left.
			// Read 1024 bytes of data at once and write that data and read again and so on.
			// if no data is left then it will return -1
			/*
			 * byte[] b = new byte[1024]; while(in.read(b) > 0) { fos.write(b); } // code
			 * provided by apache so we are using that in below line
			 */
			IOUtils.copy(in, fos);
			fos.close();
			in.close();
		}
	}
}