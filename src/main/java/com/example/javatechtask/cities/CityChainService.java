package com.example.javatechtask.cities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityChainService {

    private final ResourceLoader resourceLoader;

    public CityChainService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String findLongestCityChain(String filePath) throws IOException {
        List<String> cities = readCitiesFromFile(filePath);
        System.out.println(findLongestChain(cities));
        return findLongestChain(cities);
    }

    private List<String> readCitiesFromFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        List<String> cities;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            cities = reader.lines()
                    .map(String::trim)
                    .map(this::capitalizeFirstLetter)
                    .collect(Collectors.toList());
        }
        return cities;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private String findLongestChain(List<String> cities) {
        Map<Character, List<String>> startCharMap = cities.stream()
                .collect(Collectors.groupingBy(city -> Character.toLowerCase(city.charAt(0))));

        Map<String, List<String>> adjList = cities.stream()
                .collect(Collectors.toMap(city -> city, city -> {
                    char endChar = Character.toLowerCase(city.charAt(city.length() - 1));
                    return startCharMap.getOrDefault(endChar, Collections.emptyList()).stream()
                            .filter(neighbor -> !city.equalsIgnoreCase(neighbor))
                            .collect(Collectors.toList());
                }));

        String[] longestChain = new String[]{""};
        cities.forEach(city -> buildChain(city, adjList, new HashSet<>(), city, longestChain));
        return longestChain[0].trim();
    }

    private void buildChain(String currentCity, Map<String, List<String>> adjList, Set<String> visited,
                            String currentChain, String[] longestChain) {
        visited.add(currentCity);

        if (currentChain.length() > longestChain[0].length()) {
            longestChain[0] = currentChain;
        }

        adjList.get(currentCity).stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .forEach(neighbor -> buildChain(neighbor, adjList, visited, currentChain + " " + neighbor, longestChain));

        visited.remove(currentCity);
    }
}
