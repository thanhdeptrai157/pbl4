
package org.Server.Model.DAL.DAO.implemet;

import java.util.List;

import org.Server.Model.DAL.DAO.AbstractDao;
import org.Server.Model.DAL.Mapper.ClassesPeriodMapper;
import org.Server.Model.DAL.Repository.Repository;
import org.Server.Model.DAL.Specification.Specification;
import org.Server.Model.DTO.ClassPeriod;


public class ClassPeriodDAO extends AbstractDao implements Repository<ClassPeriod>{
    @Override
    public void add(ClassPeriod t) {
        String query = "INSERT INTO class_period VALUES (?, ?, ?, ?)";

        insert(query, t.getId_class_period(), t.getId_class(), t.getTime_start(), t.getTime_end());
    }

    @Override
    public List<ClassPeriod> getALL() {
        String query = "SELECT * FROM class_period ";
        List<ClassPeriod> list = query(query, new ClassesPeriodMapper());
        return list;
    }

    @Override
    public List<ClassPeriod> findBySpacification(Specification<ClassPeriod> specification) {
        List<ClassPeriod> list = query(specification.getQuery(), new ClassesPeriodMapper(), specification.getParameters());

        return list;
    }

    @Override
    public void update(ClassPeriod t) {
    }

    @Override
    public void remove(ClassPeriod t) {
        String query = "DELETE FROM class_period WHERE id_class_period = ?";
        update(query, t.getId_class_period());
    }


}
