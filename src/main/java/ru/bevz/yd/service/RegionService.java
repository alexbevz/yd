package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.model.Region;
import ru.bevz.yd.repository.RegionRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {

    @Autowired
    private RegionRepository regionRep;

    public Region addIfNotExistsRegion(Region region) {
        int number = region.getNumber();

        if (regionRep.existsRegionsByNumber(number)) {
            region = regionRep.getRegionByNumber(number);
        } else {
            region = regionRep.save(region);
        }

        return region;
    }

    public Set<Region> addIfNotExistsRegions(Set<Region> regionList) {
        return regionList
                .stream()
                .map(this::addIfNotExistsRegion)
                .collect(Collectors.toSet());
    }

}
