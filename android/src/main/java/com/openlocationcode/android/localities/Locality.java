/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openlocationcode.android.localities;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.openlocationcode.OpenLocationCode;
import com.google.openlocationcode.OpenLocationCode.CodeArea;

/**
 * Return the nearest locality within 0.4 degrees latitude and longitude.
 */
public class Locality {
    // Localities more than this distance in either latitude or longitude are too far away.
    private static final double MAX_DISTANCE_DEGREES = 0.4;

    private static final String TAG = "Locality";

    /**
     * Thrown when no locality nearby exists.
     */
    public static final class NoLocalityException extends Exception {
    }

    /**
     * Returns the locality name nearest to the passed location or throws
     * a NoLocalityException if none are near enough to be used.
     */
    public static String getNearestLocality(OpenLocationCode location) throws NoLocalityException {
        String nearestLocality = null;
        double distanceDegrees = Float.MAX_VALUE;
        CodeArea locationArea = location.decode();
        // Scan through the localities to find the nearest.
        for (int i = 0; i < LOCALITIES.size(); i++) {
            String localityEntry = LOCALITIES.get(i);
            String[] parts = localityEntry.split(":");
            OpenLocationCode localityCode = new OpenLocationCode(parts[0]);
            CodeArea localityArea = localityCode.decode();
            double localityDistanceDegrees = Math.max(
                    Math.abs(localityArea.getCenterLatitude() - locationArea.getCenterLatitude()),
                    Math.abs(localityArea.getCenterLongitude() - locationArea.getCenterLongitude())
            );
            if (localityDistanceDegrees >= MAX_DISTANCE_DEGREES) {
                continue;
            }
            if (localityDistanceDegrees < distanceDegrees) {
                nearestLocality = parts[1];
                distanceDegrees = localityDistanceDegrees;
            }
        }
        if (nearestLocality == null) {
            throw new NoLocalityException();
        }
        return nearestLocality;
    }

    /**
     * @param localitySearch The locality name to search for.
     * @return the locality full code, or an empty string if not found.
     */
    public static String getLocalityCode(@NonNull String localitySearch) {
        for (String locality : LOCALITIES) {
            if (locality.subSequence(10, locality.length()).equals(localitySearch)) {
                return locality.subSequence(0, 9).toString();
            }
        }
        return "";
    }

    /**
     * Note: The search for currentLocation and mapLocation locality is done locally only.
     *
     * @param currentLocation If not null, the locality of the {@code currentLocation} will appear
     *                        first in the list
     * @param mapLocation     If not null, the locality of the {@code mapLocation} will appear next
     *                        in the list
     * @return the list of localities to show in the search suggestions
     */
    public static List<String> getAllLocalitiesForSearchDisplay(
            OpenLocationCode currentLocation, OpenLocationCode mapLocation) {
        List<String> allLocalities = new ArrayList<>(LOCALITIES_WITHOUT_CODE_ALPHABETICAL);
        if (mapLocation != null) {
            String mapLocationLocality;
            try {
                mapLocationLocality = getNearestLocality(mapLocation);
                int index = allLocalities.indexOf(mapLocationLocality);
                allLocalities.remove(index);
                allLocalities.add(0, mapLocationLocality);
            } catch (NoLocalityException e) {
                Log.d(TAG, "map not centered on CV");
            }
        }
        if (currentLocation != null) {
            String currentLocationLocality;
            try {
                currentLocationLocality = getNearestLocality(currentLocation);
                int index = allLocalities.indexOf(currentLocationLocality);
                allLocalities.remove(index);
                allLocalities.add(0, currentLocationLocality);
            } catch (NoLocalityException e) {
                Log.d(TAG, "current location not in CV");
            }
        }
        return allLocalities;
    }

    // List of OLC codes and locality names. This must be sorted by OLC code.
    private static final List<String> LOCALITIES = Arrays.asList(
            "796QR7P7+:Lomba Tantum",
            "796QR7R7+:Palhal",
            "796QR7XH+:Campo Baixo",
            "796QR8P4+:Cacha??o",
            "796QRJXV+:Fonte Aleixo",
            "796QV72G+:Escovinha",
            "796QV74J+:Nossa Senhora do Monte",
            "796QV77Q+:Cova de Joana",
            "796QV7C9+:Faj?? de ??gua",
            "796QV7CW+:Cova Rodela",
            "796QV884+:Mato Grande",
            "796QV8C4+:Nova Sintra",
            "796QV8F7+:Santa B??rbara",
            "796QV8G2+:Lem",
            "796QV8Q9+:Furna",
            "796QVGRX+:Forno",
            "796QVGW4+:S??o Filipe",
            "796QVH7J+:Salto",
            "796QVHFG+:Patim",
            "796QVHH6+:Penteada",
            "796QVJ45+:Achada Poio",
            "796QVJC8+:Monte Largo",
            "796QVJFR+:Achada Furna",
            "796QVM4F+:Dacabalaio",
            "796QVMCR+:Figueira Pav??o",
            "796QVMRP+:Mae Joana",
            "796QVMWH+:Estancia Rogue",
            "796QVPQ4+:Cova Figueira",
            "796QWGCX+:Lagari??a",
            "796QWGGF+:Tongon",
            "796QWGJQ+:Cerrado",
            "796QWGQJ+:Logar Novo",
            "796QWHV5+:Nhuco",
            "796QWPH3+:Tinteira",
            "796QX896+:Ilh??u Grande",
            "796QXG8V+:Italiano",
            "796QXHH4+:Mira-Mira",
            "796QXMQW+:Achada Grande",
            "796QXPF3+:Relva",
            "796RW8VP+:Porto Gouveia",
            "796RW98W+:Cidade Velha",
            "796RWCCH+:Costa D' Achada",
            "796RWCXQ+:Dias",
            "796RWFMP+:Praia",
            "796RX828+:Porto Mosquito",
            "796RX9Q6+:Santa Ana",
            "796RXC5Q+:Trindade",
            "796RXC69+:Jo??o Varela",
            "796RXCGM+:Ribeirinha",
            "796RXF8G+:Sao Filipe",
            "796RXFM2+:Cambujana",
            "796RXFM6+:Veneza",
            "796RXFW6+:Achada Venteiro",
            "796RXG3F+:Sao Tome",
            "796RXGH5+:S??o Francisco",
            "797Q2H22+:Galinheiros",
            "797Q2H7F+:S??o Jorge",
            "797Q2H8R+:Campanas de Baixo",
            "797Q2JG6+:Atalaia",
            "797Q2JQF+:Ribeira Ilh??u",
            "797Q2M2W+:Corvo",
            "797Q2MCP+:Fonsaco",
            "797Q2MJF+:Mosteiros",
            "797Q2MV5+:Faj??zinha",
            "797R28RX+:Pico Leao",
            "797R2CHP+:S??o Domingos",
            "797R2CP2+:Rui Vaz",
            "797R2F5C+:Curral Grande",
            "797R2FHJ+:Milho Branco",
            "797R2GJ7+:Portal",
            "797R2GM4+:Capela",
            "797R2GQC+:Cancelo",
            "797R2GX2+:Mato Jorge",
            "797R2HC2+:Moia Moia",
            "797R366M+:Ponta Rinc??o",
            "797R37PV+:Achada Gregorio",
            "797R37VV+:Chao de Tanque",
            "797R38G2+:Palha Carga",
            "797R38XH+:Assomada",
            "797R393X+:S??o Jorge dos ??rg??os",
            "797R39J7+:Picos",
            "797R3CHP+:Ribeira Seca",
            "797R3CP2+:Galeao",
            "797R3CQG+:Liberao",
            "797R3F77+:Cruz do Gato",
            "797R3FH9+:Salas",
            "797R3FHC+:Joao Teves",
            "797R3FPC+:Renque Purga",
            "797R3FQ2+:Poilao",
            "797R3G8C+:Praia Baixo",
            "797R46PV+:Ribeira da Barca",
            "797R476G+:Tomba Toiro",
            "797R476R+:Ribao Manuel",
            "797R48GH+:Boa Entrada",
            "797R4988+:Jalalo",
            "797R499J+:Tribuna",
            "797R49FR+:Rebelo",
            "797R49W5+:Saltos de Cima",
            "797R49XP+:Saltos de Baixo",
            "797R4C4C+:Cudelho",
            "797R4CG2+:Serelbo",
            "797R4CM3+:Toril",
            "797R4CMR+:Santa Cruz",
            "797R4F9J+:Achada Fazenda",
            "797R4FP8+:Pedra Badejo",
            "797R4QQV+:Vila do Maio",
            "797R4RPQ+:Barreiro",
            "797R4VVH+:Ribeira Dom Jo??o",
            "797R57M9+:Figueira da Naus",
            "797R57R7+:Figueira das Naus",
            "797R584P+:Joao Dias",
            "797R5872+:Fundura",
            "797R5967+:Flamengos",
            "797R59J4+:Ribeira de Sao Miquel",
            "797R59W8+:Pilao Cao",
            "797R5C2Q+:Cancelo",
            "797R5CP5+:Calheta de S??o Miguel",
            "797R5QHG+:Morro",
            "797R5R6X+:Figueira da Horta",
            "797R67H4+:Ribeira da Prata",
            "797R67QC+:Milho Branco",
            "797R683H+:Ribeira Principal",
            "797R6896+:Lagoa",
            "797R68R7+:Massa Pe",
            "797R68VX+:Achada Tenda",
            "797R69J4+:Mangue de Setes Ribeiras",
            "797R6QHP+:Calheta",
            "797R6V5W+:Pil??o C??o",
            "797R6V8W+:Alcatraz",
            "797R6VXM+:Pedro Vaz",
            "797R7765+:Ch??o Bom",
            "797R77G2+:Tarrafal",
            "797R77VP+:Tr??s os Montes",
            "797R7845+:Biscainhos",
            "797R7Q8W+:Morrinho",
            "797R7RCG+:Cascabulho",
            "797R7V8P+:Santo Ant??nio",
            "797VX6P6+:Curral Velho",
            "798PRWJQ+:S??o Pedro",
            "798PVXGH+:Lazareto",
            "798PXM5R+:Tarrafal de Monte Trigo",
            "798PXQ97+:Jo??o Daninha",
            "798PXQ9P+:??gua das Fortes",
            "798PXQWJ+:Lombo de Torre",
            "798PXRW2+:Baboso",
            "798PXV93+:Pedrinha",
            "798QHJ7W+:Tarrafal de S??o Nicolau",
            "798QHJGQ+:Escada",
            "798QHP79+:Pregui??a",
            "798QHPWV+:Cruz de Baixo",
            "798QHWQ2+:Urzuleiros",
            "798QHWX5+:Jalunga",
            "798QHXM5+:Castilhiano",
            "798QJC87+:Ilh??u Raso",
            "798QJH2Q+:Barril",
            "798QJJV5+:Praia Branca",
            "798QJM2V+:Cabe??alinho",
            "798QJM3X+:Calej??o",
            "798QJMC7+:Cacha??o",
            "798QJMR7+:Faj?? de Cima",
            "798QJMVM+:Queimada",
            "798QJMXG+:Faj?? de Baixo",
            "798QJMXW+:Carvoeiro",
            "798QJP83+:Ribeira Brava",
            "798QJQJC+:Bel??m",
            "798QJRJ4+:Morro Br??s",
            "798QJV79+:Juncalinho",
            "798QM84H+:Ilh??u Branco",
            "798QMJ5P+:Ribeira da Prata",
            "798QMM9H+:Est??ncia de Br??s",
            "798QMMF7+:Ribeira Funda",
            "798QQ734+:Ponta da Cruz",
            "798QQ774+:Ilha de Santa Luzia",
            "798QR2VM+:Ermida",
            "798QR3J9+:Madeiral",
            "798QV2G9+:Mindelo",
            "798QV43P+:Ribeira de Calhau",
            "798QW333+:Salamansa",
            "798QW33R+:Ba??a das Gatas",
            "798V23PM+:Povoa????o Velha",
            "798V27G3+:Jo??o Barrosa",
            "798V358M+:S??o Jorge",
            "798V35WR+:Ilha da Boa Vista",
            "798V44J7+:Rabil",
            "798V4588+:Amador",
            "798V47FG+:Cabe??a dos Tarrafes",
            "798V47QF+:Fundo das Figueiras",
            "798V53MP+:Sal Rei",
            "798V54JX+:Ponta Adiante",
            "798V55PG+:Bofareira",
            "798V5724+:Jo??o Galego",
            "798V57WH+:Gata",
            "798V6652+:Espingueira",
            "798V66F3+:Derrubado",
            "798VJ32Q+:Santa Maria",
            "798VM3H8+:Murdeira",
            "798VQ25C+:Palmeira",
            "798VQ32X+:Feijoal",
            "798VQ346+:Espargos",
            "798VQ474+:Pedra Lume",
            "799P2MHH+:Monte Trigo",
            "799P2QRV+:Cha de Morte",
            "799P2QVR+:Curral das Vacas",
            "799P2QXH+:Cirio",
            "799P2R42+:Leiro",
            "799P2WCM+:Porto Novo",
            "799P3MPP+:Bonita",
            "799P3PHR+:Lascado",
            "799P3PM6+:Ribeira Vermellia",
            "799P3QMG+:Miguel Pires",
            "799P3QR3+:Martiene",
            "799P3R6V+:Ribeira Fria",
            "799P3RJQ+:Tabuadinha",
            "799P3WXM+:Lombo de Figueira",
            "799P4Q54+:Ribeira da Cruz",
            "799P4QVQ+:Figueiras de Baixo",
            "799P4RCQ+:Esgamaleiro",
            "799P4VJG+:Caibros de Ribeira de Jorge",
            "799P4VXX+:Figueiral de Cima",
            "799P4W5J+:Faja de Cima",
            "799P4WQ7+:Espongeiro",
            "799P4XXM+:Paul",
            "799P5R4P+:Ch?? de Igreja",
            "799P5R9M+:Cruzinha",
            "799P5V3X+:Dagoio",
            "799P5V6J+:Boca de Coruja",
            "799P5V7W+:Boca de Curral",
            "799P5WJM+:Ribeira Grande",
            "799P5XGC+:Sinagoga",
            "799P6W24+:Ponta do Sol",
            "799Q4282+:Janela");

    private static final List<String> LOCALITIES_WITHOUT_CODE_ALPHABETICAL =
            createLocalitiesWithoutCode();

    private static List<String> createLocalitiesWithoutCode() {
        List<String> allLocalities = new ArrayList<>();
        for (String localityWithCode : LOCALITIES) {
            allLocalities.add(localityWithCode.substring(10));
        }
        Collections.sort(allLocalities);
        return allLocalities;
    }
}
