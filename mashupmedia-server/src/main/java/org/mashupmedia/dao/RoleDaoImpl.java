package org.mashupmedia.dao;

import java.util.List;

import jakarta.persistence.TypedQuery;

import org.mashupmedia.model.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends BaseDaoImpl implements RoleDao {

	@Override
	public void saveRole(Role role) {
		saveOrUpdate(role);
	}

	@Override
	public Role getRole(String idName) {
		TypedQuery<Role> query = entityManager.createQuery("from Role where idName = :idName", Role.class);
		query.setParameter("idName", idName);
		Role role = getUniqueResult(query);
		return role;
	}

	@Override
	public List<Role> getRoles() {
		TypedQuery<Role> query = entityManager.createQuery("from Role order by name", Role.class);
		List<Role> roles = query.getResultList();
		return roles;
	}

}
