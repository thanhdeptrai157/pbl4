package org.Server.Model.DAL.DAO.implemet;

import java.util.List;

import org.Server.Model.DAL.DAO.AbstractDao;
import org.Server.Model.DAL.Mapper.ClassesMapper;
import org.Server.Model.DAL.Repository.Repository;
import org.Server.Model.DAL.Specification.Specification;
import org.Server.Model.DTO.Classes;


public class ClassesDAO extends AbstractDao implements Repository<Classes>{
    @Override
    public void add(Classes t) {
        String query = "INSERT INTO class VALUES (?, ?, ?)";

        insert(query, t.getId_class(), t.getName_class(), t.getPassword());
    }

    @Override
    public List<Classes> getALL() {
        String query = "SELECT * FROM class ";
        List<Classes> list = query(query, new ClassesMapper());
        return list;
    }

    @Override
    public List<Classes> findBySpacification(Specification<Classes> specification) {
        List<Classes> list = query(specification.getQuery(), new ClassesMapper(), specification.getParameters());

        return list;
    }

    @Override
    public void update(Classes t) {
        String query = "UPDATE class SET name_class = ? WHERE id_class = ?";
        update(query, t.getName_class(), t.getId_class());
    }

    @Override
    public void remove(Classes t) {
        String query = "DELETE FROM class WHERE id_class = ?";
        update(query, t.getId_class());
    }


}
