package dima.java.gameeliminiationdetector;

/* *****************************************************************************
 *  Name: Dumitru Hanciu
 *  Date: 22.01.2019
 *  Description: BaseballElimination
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {
    private int n;
    private int[] wins;
    private int[] loses;
    private int[] games;
    private int[][] game;
    private boolean[] isOut;
    private String[] teamsTable;
    private Map<String, Integer> teams;
    private Map<Integer, Set<String>> certificate;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In in = new In(filename);
        n = in.readInt();
        wins = new int[n];
        loses = new int[n];
        games = new int[n];
        game = new int[n][n];
        isOut = new boolean[n];
        teams = new HashMap<>(n);
        teamsTable = new String[n];
        certificate = new HashMap<>();

        for (int i = 0; i < n; i++) {
            teamsTable[i] = in.readString();
            teams.put(teamsTable[i], i);
            wins[i] = in.readInt();
            loses[i] = in.readInt();
            games[i] = in.readInt();
            isOut[i] = false;

            for (int j = 0; j < n; j++)
                game[i][j] = in.readInt();
        }

        for (String team : teams.keySet())
            trivialCompute(team);

        for (String team : teamsTable)
            minCutCompute(team);
    }

    private void trivialCompute(String team) {
        for (int i = 0, id = teams.get(team); i < n; i++)
            if (i != id && wins[id] + games[id] < wins[i]) {
                isOut[id] = true;
                addTeamtoCertificate(id, teamsTable[i]);
            }
    }

    private void minCutCompute(String team) {
        int source = n * n;
        int sink = n * n + 1;
        int mainTeam = teams.get(team);
        FlowNetwork flow = new FlowNetwork(n * n + 2);

        // add edges from every team to sink
        for (int i = 0; i < n; i++)
            if (i != mainTeam) {
                int otherTeam = i * n + i;
                flow.addEdge(new FlowEdge(otherTeam, sink,
                                          Math.max(wins[mainTeam] + games[mainTeam] - wins[i], 0)));
            }

        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                if (i != mainTeam && j != mainTeam) {
                    int teamA = i * n + i;
                    int teamB = j * n + j;
                    int gameNode = i * n + j;

                    flow.addEdge(new FlowEdge(source, gameNode, game[i][j]));
                    flow.addEdge(new FlowEdge(gameNode, teamA, Integer.MAX_VALUE));
                    flow.addEdge(new FlowEdge(gameNode, teamB, Integer.MAX_VALUE));
                }

        FordFulkerson minCut = new FordFulkerson(flow, source, sink);
        for (int i = 0; i < n; i++) {
            if (i != mainTeam && minCut.inCut(i * n + i)) {
                addTeamtoCertificate(mainTeam, teamsTable[i]);
                isOut[mainTeam] = true;
            }
        }
    }

    private void addTeamtoCertificate(int teamId, String team) {
        if (!certificate.containsKey(teamId)) certificate.put(teamId, new HashSet<String>() {{
            add(team);
        }});
        else certificate.get(teamId).add(team);
    }

    // number of teams
    public int numberOfTeams() {
        return n;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teamsTable);
    }

    // number of wins for given team
    public int wins(String team) {
        validate(team);
        int teamID = getid(team);
        return wins[teamID];
    }

    // number of losses for given team
    public int losses(String team) {
        validate(team);
        int teamID = getid(team);
        return loses[teamID];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validate(team);
        int teamID = getid(team);
        return games[teamID];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        int teamID1 = getid(team1);
        int teamID2 = getid(team2);
        return game[teamID1][teamID2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validate(team);
        int teamID = getid(team);
        return isOut[teamID];
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validate(team);
        int teamID = getid(team);
        return certificate.get(teamID);
    }

    private int getid(String team) {
        return teams.get(team);
    }

    private void validate(String team) {
        if (team == null || !teams.containsKey(team))
            throw new IllegalArgumentException();
    }

    // test client
    public static void main(String[] args) {
        StdOut.print();
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
