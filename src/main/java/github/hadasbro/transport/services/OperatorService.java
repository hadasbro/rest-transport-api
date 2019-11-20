package github.hadasbro.transport.services;

import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.domain.transport.Owner;
import github.hadasbro.transport.domain.transport.Vehicle;
import github.hadasbro.transport.domain.location.City;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.exceptions.ValidationException;
import github.hadasbro.transport.repository.OperatorRepository;
import github.hadasbro.transport.repository.VehicleRepository;
import github.hadasbro.transport.repository.OwnerRepository;
import github.hadasbro.transport.webDto.OperatorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * OperatorService
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Service
public class OperatorService {

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * setOperatorData
     *
     * @param operatorDto -
     * @return -
     * @throws ValidationException -
     */
    private Operator setOperatorData(OperatorDto operatorDto) throws ValidationException {
        return setOperatorData(operatorDto, null);
    }

    /**
     * setOperatorData
     *
     * @param operatorDto -
     * @return Operator
     * @throws ValidationException -
     */
    private Operator setOperatorData(OperatorDto operatorDto, Operator operator) throws ValidationException {

        Optional<Owner> operatorOwner = ownerRepository.findById(operatorDto.getOwner());
        Owner owner;

        if (operatorOwner.isEmpty() && operator == null) {
            throw new ValidationException(
                    String.format("Owner #id %d doesn't exist", operatorDto.getOwner())
            );
        } else if (operatorOwner.isPresent()) {
            owner = operatorOwner.get();
        } else {
            owner = operator.getOwner();
        }

        Operator roperator;

        roperator = Objects.requireNonNullElseGet(operator, Operator::new);

        roperator.setType(operatorDto.getType());
        roperator.setOperatorCode(operatorDto.getOperatorCode());
        roperator.setName(operatorDto.getName());
        roperator.setOwner(owner);
        roperator.setLicenceId(operatorDto.getLicenceId());

        return roperator;

    }

    /**
     * createNew
     *
     * @param operatorDto -
     * @return Operator
     * @throws ValidationException -
     */
    public Operator create(OperatorDto operatorDto) throws ValidationException {

        Operator operator = setOperatorData(operatorDto);

        return operatorRepository.save(operator);

    }

    /**
     * add
     *
     * @param operator -
     * @return Operator
     */
    public Operator add(Operator operator) {
        return operatorRepository.save(operator);
    }

    /**
     * add
     *
     * @param operators -
     * @return List<Operator>
     */
    public List<Operator> add(Set<Operator> operators) {
        return operatorRepository.saveAll(operators);
    }

    /**
     * updade
     *
     * @param operatorDto -
     * @param id      -
     * @return Operator
     * @throws ValidationException -
     */
    public Operator updade(OperatorDto operatorDto, Long id) throws ValidationException {

        Optional<Operator> operator = operatorRepository.findById(id);

        if (operator.isEmpty()) {
            throw new ValidationException(
                    String.format("Operator #id %d doesn't exist", id)
            );
        }

        Operator operatorMerged = setOperatorData(operatorDto, operator.get());

        return operatorRepository.save(operatorMerged);

    }

    /**
     * delete
     * @param id -
     * @throws ValidationException -
     */
    public void delete(Long id) throws ValidationException {

        Optional<Operator> operatorOpt = operatorRepository.findById(id);

        if (operatorOpt.isEmpty()) {
            throw new ValidationException(
                    String.format("Operator #id %d doesn't exist", id)
            );
        }

        operatorRepository.delete(operatorOpt.get());
    }

    /**
     * findPaginated
     *
     * @param page -
     * @param size -
     * @return List<Operator>
     */
    public List<Operator> findPaginated(int page, int size) {
        return findPaginated(page, size, new Sort(Sort.Direction.ASC, "id"));
    }

    /**
     * findPaginated
     *
     * @param page -
     * @param size -
     * @param sort -
     * @return List<Operator>
     */
    public List<Operator> findPaginated(int page, int size, Sort sort) {

        Page<Operator> gPage = operatorRepository.findAll(PageRequest.of(page, size, sort));

        return gPage.getContent();

    }

    /**
     * findAll
     *
     * @return List<Operator>
     */
    public List<Operator> findAll() {
        return operatorRepository.findAll(new Sort(Sort.Direction.ASC, "id"));
    }

    /**
     * findAll
     *
     * @param sort -
     * @return List<Operator>
     */
    public List<Operator> findAll(Sort sort) {

        if (sort == null) {
            sort = new Sort(Sort.Direction.ASC, "id");
        }

        return operatorRepository.findAll(sort);

    }

    /**
     * findFirstByLicenceIdAndType
     *
     * @param licenceId -
     * @param type -
     * @return Optional<Operator>
     */
    public Optional<Operator> findFirstByLicenceIdAndType(Integer licenceId, Integer type) {

        if(type != null){
            return operatorRepository.findFirstByLicenceIdAndType(licenceId, type);
        } else {
            return operatorRepository.findFirstByLicenceId(licenceId);
        }

    }

    /**
     * findOneById
     *
     * @param id -
     * @return Operator
     */
    public Operator findOneById(Long id) {

        Optional<Operator> opt = operatorRepository.findById(id);

        return opt.orElse(null);

    }

    /**
     * findOwnerById
     *
     * @param id -
     * @return Owner
     */
    public Owner findOwnerById(Long id) {

        Optional<Owner> operatorOpt = ownerRepository.findById(id);

        return operatorOpt.orElse(null);
    }

    /**
     * findOperatorByData
     *
     * @param code     -
     * @param owner -
     * @return Operator
     */
    public Operator findOperatorByData(String code, Owner owner) {
        return findOperatorByData(code, owner, null);
    }

    /**
     * findOperatorByData
     *
     * @param code     -
     * @param owner -
     * @param type  -
     * @return Operator
     */
    public Operator findOperatorByData(String code, Owner owner, Integer type) {

        Optional<Operator> operator;

        if (type == null) {
            operator = operatorRepository.findFirstByOperatorCodeAndOwnerId(code, owner.getId());
        } else {
            operator = operatorRepository.findFirstByOperatorCodeAndOwnerIdAndType(code, owner.getId(), type);
        }

        return operator.orElse(null);
    }

    /**
     * findByCodes
     *
     * @param codes -
     * @return Set<Operator>
     */
    public Set<Operator> findByCodes(List<String> codes) {
        return operatorRepository.findByOperatorCodeIn(codes);
    }

    /**
     * getOperatorsWithVehiclesAndOwners
     *
     * @param vehicles     -
     * @param owners -
     * @return Set<Operator> -
     */
    public Set<Operator> getOperatorsWithVehiclesAndOwners(Set<String> vehicles, Set<String> owners) {
        return operatorRepository.getOperatorsWithVehiclesAndOwners(vehicles, owners);
    }

    /**
     * getVehicles
     *
     * @param vehiclesTypes -
     * @param ownerSlugs -
     * @return List<Vehicle>
     */
    public Set<Vehicle> getOperatorVehiclesByOwners(Set<String> vehiclesTypes, Set<String> ownerSlugs) {
        return operatorRepository.getOperatorVehiclesByOwners(vehiclesTypes, ownerSlugs);
    }

    /**
     * checkIfCodeIsAvailable
     *
     * @param operatorCode -
     * @return boolean
     */
    public boolean checkIfCodeIsAvailable(String operatorCode) {
        return !(operatorRepository.operatorCodeCount(operatorCode) >= 1);
    }

    /**
     * addOperatorOwners
     *
     * @param owners -
     * @return ArrayList<Owner>
     */
    public ArrayList<Owner> addOperatorOwners(Set<Owner> owners) {
        return new ArrayList<>(ownerRepository.saveAll(owners));

    }

    /**
     * addOperatorVehicles
     *
     * @param vehicles -
     * @return List<Vehicle>
     */
    public Set<Vehicle> addOperatorVehicles(Set<Vehicle> vehicles) {
        return new HashSet<>(vehicleRepository.saveAll(vehicles));
    }

    /**
     * countOperatorCity
     *
     * @return Long
     */
    public Long countOperatorCity(Operator operator, City city) {
        return 11L;
        // TODO return repository.countOperatorCity(operator, city);
    }

    /**
     * checkPassengerCanUseOperatorsLine
     *
     * @param operator -
     * @throws ApiException -
     */
    public void checkPassengerCanUseOperatorsLine(Operator operator, Passenger passenger) throws ApiException {

        Long cnt = countOperatorCity(operator, passenger.getCity());

        // TODO
        if (cnt == 0 && 1 != 1) {
            throw new ApiException(ApiException.CODES.PASSENGER_LINE);
        }

    }

    /**
     * checkOperatorRestrictions
     * @param operator -
     * @throws ApiException -
     */
    public void checkOperatorRestrictions(Operator operator) throws ApiException {

        if (operator.getStatus() == Operator.STATUS_INACTIVE) {
            throw new ApiException(ApiException.CODES.OPER_RESTRICTED);
        }

    }

}