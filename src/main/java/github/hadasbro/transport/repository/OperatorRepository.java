package github.hadasbro.transport.repository;


import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.domain.location.City;
import github.hadasbro.transport.domain.transport.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
@SuppressWarnings({"unused"})
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    Set<Operator> findByOperatorCodeIn(Collection<String> codes);

    Optional<Operator> findFirstByLicenceIdAndType(Integer licenceId, Integer type);

    Optional<Operator> findFirstByLicenceId(Integer licenceId);

    Optional<Operator> findFirstByOperatorCodeAndOwnerIdAndType(String code, Long ownerId, int type);

    Optional<Operator> findFirstByOperatorCodeAndOwnerId(String code, Long ownerId);

    Collection<Operator> findByNameLikeAndType(String name, int type);

    /**
     * getOperatorsWithVehiclesAndOwners
     *
     * @param vehicles -
     * @param owners -
     * @return Set<Operator>
     */
    @Query("SELECT op FROM Operator op JOIN op.vehicles ve JOIN op.owner p WHERE ve.name IN(:vehicles) AND p.slug IN(:owners)")
    Set<Operator> getOperatorsWithVehiclesAndOwners(Collection<String> vehicles, Collection<String> owners);

    /**
     * getVehicles
     *
     * @param vehicleSlugs -
     * @param ownerSlugs -
     * @return Set<Vehicle> -
     */
    @Query("SELECT ve FROM Vehicle ve JOIN ve.operators ope JOIN ope.owner ow WHERE ve.slug IN(:vehicleSlugs) AND ow.slug IN(:ownerSlugs)")
    Set<Vehicle> getOperatorVehiclesByOwners(Set<String> vehicleSlugs, Set<String> ownerSlugs);

    /**
     * operatorCodeCount
     *
     * count cities with errorCode
     *
     * @param operatorCode -
     * @return Long
     */
    @Query("SELECT COUNT(ope) FROM Operator ope WHERE ope.operatorCode=:operatorCode")
    Long operatorCodeCount(String operatorCode);

    /**
     * countOperatorCity
     *
     * @param operator -
     * @param city -
     * @return Long
     */
    @Query("SELECT COUNT(ope) FROM Operator ope JOIN ope.cities oc WHERE ope = :operator AND oc = :city")
    Long countOperatorCity(Operator operator, City city);

}