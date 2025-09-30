package com.example.dorm.service;

import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationPeriodStatus;
import com.example.dorm.repository.DormRegistrationPeriodRepository;
import com.example.dorm.repository.DormRegistrationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DormRegistrationPeriodService {

    private final DormRegistrationPeriodRepository periodRepository;
    private final DormRegistrationRequestRepository requestRepository;

    public DormRegistrationPeriodService(DormRegistrationPeriodRepository periodRepository,
                                         DormRegistrationRequestRepository requestRepository) {
        this.periodRepository = periodRepository;
        this.requestRepository = requestRepository;
    }

    public List<DormRegistrationPeriod> findAll() {
        List<DormRegistrationPeriod> periods = periodRepository.findAllByOrderByStartTimeDesc();
        periods.forEach(period -> period.setSubmittedCount(requestRepository.countByPeriodId(period.getId())));
        return periods;
    }

    public Optional<DormRegistrationPeriod> getOpenPeriod() {
        Optional<DormRegistrationPeriod> openPeriod = periodRepository
                .findFirstByStatusOrderByStartTimeDesc(DormRegistrationPeriodStatus.OPEN);

        if (openPeriod.isPresent()) {
            DormRegistrationPeriod period = openPeriod.get();
            if (period.isExpired()) {
                period.setStatus(DormRegistrationPeriodStatus.CLOSED);
                if (period.getEndTime() == null) {
                    period.setEndTime(LocalDateTime.now());
                }
                periodRepository.save(period);
                return Optional.empty();
            }
            long submitted = requestRepository.countByPeriodId(period.getId());
            period.setSubmittedCount(submitted);
            if (period.getCapacity() != null && submitted >= period.getCapacity()) {
                period.setStatus(DormRegistrationPeriodStatus.CLOSED);
                if (period.getEndTime() == null) {
                    period.setEndTime(LocalDateTime.now());
                }
                periodRepository.save(period);
                return Optional.empty();
            }
        }

        return openPeriod;
    }

    public long countSubmittedRequests(Long periodId) {
        return requestRepository.countByPeriodId(periodId);
    }

    @Transactional
    public DormRegistrationPeriod openPeriod(DormRegistrationPeriod period) {
        if (periodRepository.existsByStatus(DormRegistrationPeriodStatus.OPEN)) {
            throw new IllegalStateException("Đang có đợt đăng ký khác mở. Vui lòng đóng trước khi tạo đợt mới.");
        }

        if (period.getName() == null || period.getName().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đợt đăng ký.");
        }

        if (period.getCapacity() != null && period.getCapacity() < 1) {
            throw new IllegalArgumentException("Chỉ tiêu phải lớn hơn 0.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (period.getStartTime() == null || period.getStartTime().isAfter(now)) {
            period.setStartTime(now);
        }

        if (period.getEndTime() != null && period.getEndTime().isBefore(period.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu.");
        }

        period.setStatus(DormRegistrationPeriodStatus.OPEN);
        DormRegistrationPeriod saved = periodRepository.save(period);
        saved.setSubmittedCount(0L);
        return saved;
    }

    @Transactional
    public DormRegistrationPeriod closePeriod(Long id) {
        DormRegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt đăng ký."));

        if (period.getStatus() == DormRegistrationPeriodStatus.CLOSED) {
            return period;
        }

        period.setStatus(DormRegistrationPeriodStatus.CLOSED);
        if (period.getEndTime() == null) {
            period.setEndTime(LocalDateTime.now());
        }

        DormRegistrationPeriod saved = periodRepository.save(period);
        saved.setSubmittedCount(requestRepository.countByPeriodId(saved.getId()));
        return saved;
    }
}
