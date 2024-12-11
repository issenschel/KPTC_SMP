package com.example.kptc_smp.controller;

import com.example.kptc_smp.dto.guild.OrderDto;
import com.example.kptc_smp.entity.Order;
import com.example.kptc_smp.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GuildController {
    private final GuildService guildService;


    @PostMapping("/createNewOrder")
    public ResponseEntity<?> createNewOrder(@Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(guildService.createNewOrder(orderDto));
    }

    @GetMapping("/getOrders")
    public ResponseEntity<?> getOrders(@RequestParam(name = "page") int page) {
        return ResponseEntity.ok(guildService.getOrders(page));
    }

    @PostMapping("/changeOrder")
    public ResponseEntity<?> changeOrder(@RequestParam(name = "id") int id, @Valid @RequestBody OrderDto orderDto) {
        guildService.changeOrder(orderDto, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestParam(name = "id") int id) {
        guildService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}