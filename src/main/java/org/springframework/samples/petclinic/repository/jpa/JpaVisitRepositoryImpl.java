/*
 * Copyright 2002-2013 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of the ClinicService interface using EntityManager.
 * <p/>
 * <p>
 * The mappings are defined in "orm.xml" located in the META-INF directory.
 * 
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
public class JpaVisitRepositoryImpl implements VisitRepository {
   Logger logger = LoggerFactory.getLogger(JpaVisitRepositoryImpl.class);
   @PersistenceContext
   private EntityManager em;

   @Override
   public void save(Visit visit) {
      if (visit.getId() == null) {
         this.em.persist(visit);
      } else {
         this.em.merge(visit);
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Visit> findByPetId(Integer petId) {
      Query query = this.em.createQuery("SELECT visit FROM Visit v where v.pets.id= :id");
      query.setParameter("id", petId);
      return query.getResultList();
   }

   @Override
   public Visit findById(int id) {
      Query query = this.em.createQuery("SELECT visit FROM Visit visit where visit.id=:id");
      query.setParameter("id", id);
      try {
         return (Visit) query.getSingleResult();
      } catch (NoResultException ex) {
         throw new ResourceNotFoundException();
      }
   }

   @Override
   public int delete(Visit visit) {
      removeVisitFromPet(visit);
      this.em.remove(visit);
      return visit.getId();
   }

   private void removeVisitFromPet(Visit visit) {
      Pet pet = visit.getPet();
      pet.removeVisit(visit);
   }

}
