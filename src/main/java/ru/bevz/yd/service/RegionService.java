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
    private RegionRepository regionRepository;


    public Region addIfNotExistsRegion(Region region) {
        int numberRegion = region.getNumberRegion();

        if (regionRepository.existsRegionsByNumberRegion(numberRegion)) {
            region = regionRepository.getRegionByNumberRegion(numberRegion);
        } else {
            region = regionRepository.save(region);
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
