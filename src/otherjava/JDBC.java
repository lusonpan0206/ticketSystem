package otherjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//JDBC��������ϵͳ�е�MySQL���ݿ�,���������ݲ��롢��ȡ�ķ���
class JDBC {
	//connection�����ݿ������
	private Connection connection = null;
	//statement�����ݿ���ʵ����ʵ��
	private Statement statement = null;
	//������Ϣ��
	private String citytable = "citytable";
	//�й����չ�˾��Ϣ��
	private String chinacompanytable = "chinacompanytable";
	//���캯��
	JDBC() {
		try {
			String driverName="com.mysql.jdbc.Driver";
		    String userName="root";
		    String userPasswd="";
		    //String userPasswd="";
		    String dbName="flightsystem";
		    String url="jdbc:mysql://:3306/" + dbName + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";//"+dbName+"?user="+userName+"&password="+userPasswd;

		    Class.forName(driverName);//ʵ����MySql���ݿ���������(�����м��)
		    connection=DriverManager.getConnection(url,userName,userPasswd);//�������ݿ⣬���Һ��ʵ���������
		    statement = connection.createStatement();//�ύsql���,����һ��Statement��������SQL��䷢�͵����ݿ�  
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*��ȡ���б���,�����ݿ��в����ڸó��з���Null*/
	String getCityCode(String city) {
		String sqlQuery = "select * from " + citytable + " where name='" + city + "';";
		ResultSet rs = null;
		String code = null;
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 != rs.getRow()) {
				rs = statement.executeQuery(sqlQuery);
				rs.next();
				code = rs.getString("code");
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	/*����������ڴ���*/
	void insertContinent(String country, String continent) {
		String sqlInsert = "update " + citytable + " set continent='" + continent + "' where country='" + country + "';"; 
		try {
			statement.executeUpdate(sqlInsert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*������б��롢�������ơ��������ڹ���*/
	void insertCityCode(String cityName, String cityCode, String country) {
		String sqlQuery = "select * from " + citytable + " where code ='" + cityCode + "';";
		String sqlInsert = "insert into " + citytable + "(code,name,country) values('" + cityCode + 
				"','" + cityName + "','" + country + "')";
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 != rs.getRow()) {
				System.out.print("�ظ���");
				rs = statement.executeQuery(sqlQuery);
				while(rs.next()) {
					System.out.println(cityName + cityCode + country);
					System.out.println("��" + rs.getString("name") + rs.getString("code") + rs.getString("country"));
				}
			}
			else {
				statement.executeUpdate(sqlInsert);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//�����й����չ�˾��Ϣ
	void insertCompany(String company) {
		String sqlQuery = "select * from " + chinacompanytable + " where company='" + company + "';";
		String sqlInsert = "insert into chinacompanytable values('" + company + "');";
		try {
			ResultSet rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				statement.executeUpdate(sqlInsert);
			}	
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�����ݿ��л�ȡ������Ϣ
	List<CityData> getCityFromDB() {
		ResultSet rs = null;
		List<CityData> dc = new ArrayList<CityData>();
		String sqlQuery = "select * from " + citytable + ";";
		try {
			rs = statement.executeQuery(sqlQuery);
			while(rs.next()) {
				String city = rs.getString("name");
				String code = rs.getString("code");
				String country = rs.getString("country");
				String continent = rs.getString("continent");
				dc.add(new CityData(city, country, code, continent));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dc;
	}
	
	//�ж�city�Ƿ�Ϊ�й�����
	synchronized boolean isChinaCity(String city) {
		ResultSet rs = null;
		String sqlQuery = "select * from " + citytable + " where country='�й�' and name='" +
				city + "';";
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//�ж�company�Ƿ�Ϊ�й����չ�˾
	synchronized boolean isChinaFlight(String company) {
		ResultSet rs = null;
		String sqlQuery = "select * from " + chinacompanytable + " where company='" +
				company + "';";
		try {
			rs = statement.executeQuery(sqlQuery);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//�ж��������Ƿ�����ͬһ����
	synchronized boolean isInSameCountry(String city1,String city2) {
		ResultSet rs = null;
		String sqlQuery1 = "select * from " + citytable + " where name='" +
				city1 + "';";
		String sqlQuery2 = "select * from " + citytable + " where name='" +
				city2 + "';";
		try {
			rs = statement.executeQuery(sqlQuery1);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			String country = rs.getString("country");
			rs.close();
			rs = statement.executeQuery(sqlQuery2);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			if(country.equals(rs.getString("country"))) {
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//�ж��������Ƿ�����ͬһ����
	synchronized boolean isInSameContinent(String city1,String city2) {
		ResultSet rs = null;
		String sqlQuery1 = "select * from " + citytable + " where name='" +
				city1 + "';";
		String sqlQuery2 = "select * from " + citytable + " where name='" +
				city2 + "';";
		try {
			rs = statement.executeQuery(sqlQuery1);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			String continent = rs.getString("continent");
			rs.close();
			rs = statement.executeQuery(sqlQuery2);
			rs.last();
			if(0 == rs.getRow()) {
				return false;
			}
			if(continent.equals(rs.getString("continent"))) {
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//�ͷ����ݿ�����
	void close() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}