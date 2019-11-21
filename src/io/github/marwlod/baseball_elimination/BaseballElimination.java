package io.github.marwlod.baseball_elimination;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private static final double EPSILON = 1E-6;
    private final Map<String, Integer> teamIndexMap;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException("Filename cannot be null");
        // assuming the file always has predefined format
        In in = new In(filename);
        int teamCount = in.readInt();
        teamIndexMap = new HashMap<>();
        teams = new String[teamCount];
        wins = new int[teamCount];
        losses = new int[teamCount];
        remaining = new int[teamCount];
        games = new int[teamCount][teamCount];
        for (int i = 0; i < teamCount; i++) {
            String team = in.readString();
            teamIndexMap.put(team, i);
            teams[i] = team;
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < teamCount; j++) {
                games[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teams.length;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        if (team == null || teamIndexMap.get(team) == null) throw new IllegalArgumentException("Invalid team");
        return wins[teamIndexMap.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (team == null || teamIndexMap.get(team) == null) throw new IllegalArgumentException("Invalid team");
        return losses[teamIndexMap.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (team == null || teamIndexMap.get(team) == null) throw new IllegalArgumentException("Invalid team");
        return remaining[teamIndexMap.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null || teamIndexMap.get(team1) == null || teamIndexMap.get(team2) == null)
            throw new IllegalArgumentException("Both teams must be valid teams");
        return games[teamIndexMap.get(team1)][teamIndexMap.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null || teamIndexMap.get(team) == null) throw new IllegalArgumentException("Invalid team");
        String eliminatingTeam = getTriviallyEliminatingTeam(team);
        if (eliminatingTeam != null) return true;
        FlowNetwork flowNetwork = buildFlowNetwork(team);
        Iterable<FlowEdge> adjToSource = flowNetwork.adj(0);
        int targetMaxFlow = 0;
        for (FlowEdge edge : adjToSource) {
            targetMaxFlow += edge.capacity();
        }
        FordFulkerson ff = new FordFulkerson(flowNetwork, 0, flowNetwork.V()-1);
        return Math.abs(ff.value() - targetMaxFlow) > EPSILON;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || teamIndexMap.get(team) == null) throw new IllegalArgumentException("Invalid team");
        String eliminatingTeam = getTriviallyEliminatingTeam(team);
        if (eliminatingTeam != null) return Collections.singletonList(eliminatingTeam);

        FlowNetwork flowNetwork = buildFlowNetwork(team);
        FordFulkerson ff = new FordFulkerson(flowNetwork, 0, flowNetwork.V()-1);
        List<String> eliminatingTeams = new ArrayList<>();
        int firstTeamIndex = 1 + (teams.length * (teams.length-1) / 2);
        for (int i = firstTeamIndex; i < flowNetwork.V()-1; i++) {
            if (i - firstTeamIndex == teamIndexMap.get(team)) continue;
            if (ff.inCut(i)) {
                eliminatingTeams.add(teams[i - firstTeamIndex]);
            }
        }
        if (eliminatingTeams.isEmpty()) return null;
        return eliminatingTeams;
    }

    private FlowNetwork buildFlowNetwork(String team) {
        // source + number of 2-combinations of teams + teams + sink
        int teamCombinations = teams.length * (teams.length-1) / 2;
        FlowNetwork flowNetwork = new FlowNetwork(1 + teamCombinations + teams.length + 1);
        int combinationCounter = 1;
        final int teamCounter = 1 + teamCombinations;
        for (int i = 0; i < teams.length; i++) {
            // if query index found, don't add any edges to it's vertex
            if (i == teamIndexMap.get(team)) continue;

            for (int j = i+1; j < teams.length; j++) {
                if (j == teamIndexMap.get(team)) continue;
                int gamesLeftBetweenTwoTeams = games[i][j];
                // add edge from source to combination
                flowNetwork.addEdge(new FlowEdge(0, combinationCounter, gamesLeftBetweenTwoTeams));
                // add two edges from combination (eg. {team0,team1}) to corresponding vertices (in this case to team0 and to team1)
                flowNetwork.addEdge(new FlowEdge(combinationCounter, teamCounter + i,
                        Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(combinationCounter, teamCounter + j,
                        Double.POSITIVE_INFINITY));
                combinationCounter++;
            }
        }

        final int sinkIndex = flowNetwork.V()-1;
        int queryTeamIndex = teamIndexMap.get(team);
        for (int i = 0; i < teams.length; i++) {
            if (i == teamIndexMap.get(team)) {
                continue;
            }
            // maximum number of winning games this team can get, so the query team will still have a chance to be the winner
            int numGamesStillCanWin = wins[queryTeamIndex] + remaining[queryTeamIndex]
                    - wins[i];
            // add edge from this team to sink
            flowNetwork.addEdge(new FlowEdge(teamCounter + i, sinkIndex, Math.max(numGamesStillCanWin, 0)));
        }
        return flowNetwork;
    }

    private String getTriviallyEliminatingTeam(String team) {
        int queryTeamIndex = teamIndexMap.get(team);
        int queryTeamMaxWins = wins[queryTeamIndex] + remaining[queryTeamIndex];
        int otherTeamsMaxWins = Integer.MIN_VALUE;
        int eliminatingTeam = 0;
        for (int i = 0; i < teams.length; i++) {
            if (i == queryTeamIndex) continue;
            if (wins[i] > otherTeamsMaxWins) {
                otherTeamsMaxWins = wins[i];
                eliminatingTeam = i;
            }
        }
        if (otherTeamsMaxWins > queryTeamMaxWins) return teams[eliminatingTeam];
        return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
