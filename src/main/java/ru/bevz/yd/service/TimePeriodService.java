package ru.bevz.yd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bevz.yd.repository.TimePeriodRepository;

@Service
public class TimePeriodService {

    @Autowired
    private TimePeriodRepository timePeriodRepository;

}
