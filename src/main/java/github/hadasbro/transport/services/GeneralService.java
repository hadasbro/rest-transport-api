package github.hadasbro.transport.services;
import github.hadasbro.transport.domain.location.City;
import github.hadasbro.transport.domain.location.Country;
import github.hadasbro.transport.domain.logger.GeneralLogger;
import github.hadasbro.transport.domain.logger.ApiLogger;
import github.hadasbro.transport.repository.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log
@Service
public class GeneralService {

    @Autowired
    private GeneralRepository generalRepository;

    @Autowired
    private LoggerRepository loggerRepository;

    @Autowired
    private GeneralLoggerRepository generalLoggerRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    /**
     * truncateTables
     *
     * @param models -
     */
    public void truncateTables(Class<?>[] models){
        Arrays.stream(models).forEach(el -> generalRepository.truncateDatatable(el));
    }

    /**
     * log (api)
     * @param log -
     */
    public void log(ApiLogger log) {
        loggerRepository.save(log);
    }

    /**
     * General log
     *
     * @param log -
     */
    public void log(GeneralLogger log) {
        generalLoggerRepository.save(log);
    }

    /**
     * addCities
     *
     * @param cities -
     * @return ArrayList<City>
     */
    public Set<City> addCities(Set<City> cities) {
        return new HashSet<>(cityRepository.saveAll(cities));
    }

    /**
     * addCountries
     *
     * @param countries -
     * @return Set<Country> -
     */
    public Set<Country> addCountries(Set<Country> countries) {
        return new HashSet<>(countryRepository.saveAll(countries));
    }
}