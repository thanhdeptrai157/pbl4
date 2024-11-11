package org.Server;

import java.sql.Connection;
import java.util.List;

import org.Server.Connect.JDBC_Unit;
import org.Server.Model.DAL.DAO.implemet.ClassesDAO;
import org.Server.Model.DAL.Specification.Implements.FindClassByID;
import org.Server.Model.DTO.Classes;

public class TestConnectDataBase {
    public static void main(String[] args) {
        try {
            Connection cn = JDBC_Unit.getConnection();

            ClassesDAO dao = new ClassesDAO();
            List<Classes> classes = dao.findBySpacification(new FindClassByID(1));
            for(Classes clas: classes){
                System.out.println(clas.getName_class());
            }
        } catch (Exception e) {
            System.err.println("Ket noi that bai: " + e);
        }

    }
}
