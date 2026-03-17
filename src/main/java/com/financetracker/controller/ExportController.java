package com.financetracker.controller;

import com.financetracker.model.User;
import com.financetracker.service.AuthService;
import com.financetracker.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired private ExportService exportService;
    @Autowired private AuthService authService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getCurrentUser(userDetails.getUsername());
            byte[] pdf = exportService.exportToPdf(user);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("finance-report-" + LocalDate.now() + ".pdf").build());
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authService.getCurrentUser(userDetails.getUsername());
            String csv = exportService.exportToCsv(user);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("transactions-" + LocalDate.now() + ".csv").build());
            return ResponseEntity.ok().headers(headers).body(csv.getBytes());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
