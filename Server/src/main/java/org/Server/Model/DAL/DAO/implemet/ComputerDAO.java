
package org.Server.Model.DAL.DAO.implemet;

import java.util.List;

import org.Server.Model.DAL.DAO.AbstractDao;
import org.Server.Model.DAL.Mapper.ComputerMapper;
import org.Server.Model.DAL.Repository.Repository;
import org.Server.Model.DAL.Specification.Specification;
import org.Server.Model.DTO.Computer;


public class ComputerDAO extends AbstractDao implements Repository<Computer>{
    @Override
    public void add(Computer t) {
        String query = "INSERT INTO computer VALUES (?, ?, ?)";

        insert(query, t.getId_computer(), t.getName_computer(), t.getIp_network());
    }

    @Override
    public List<Computer> getALL() {
        String query = "SELECT * FROM computer ";
        List<Computer> list = query(query, new ComputerMapper());
        return list;
    }

    @Override
    public List<Computer> findBySpacification(Specification<Computer> specification) {
        List<Computer> list = query(specification.getQuery(), new ComputerMapper(), specification.getParameters());

        return list;
    }

    @Override
    public void update(Computer t) {
        String query = "UPDATE computer class SET name_computer = ?, ip_network = ? WHERE id_computer = ?";
        update(query, t.getName_computer(), t.getIp_network(), t.getId_computer());
    }

    @Override
    public void remove(Computer t) {
        String query = "DELETE FROM computer WHERE id_computer = ?";
        update(query, t.getId_computer());
    }


}
