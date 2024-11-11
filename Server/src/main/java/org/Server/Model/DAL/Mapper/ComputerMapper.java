package org.Server.Model.DAL.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Server.Model.DTO.Computer;


public class ComputerMapper implements RowMapper<Computer>{

	@Override
	public Computer mapRow(ResultSet rs) {
		try {
			Computer computer = new Computer();
			
			computer.setId_computer(rs.getInt("id_computer"));
			computer.setName_computer(rs.getString("name_computer"));
			computer.setIp_network(rs.getString("ip_network"));
			
			return computer;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
