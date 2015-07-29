package cn.jpush.alertme.factory.plugins.nba;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2014/12/3.
 */
public class NBAUtils {
    /**
     * {date}   yyyy-MM-dd example 2014-12-03
     */
    private static String api = "http://china.nba.com/wap/static/data/scores/daily_{date}.json";


    /**
     * GAME_STATUS
     *  value 1
     *  value 2
     *  value 3 game finish
     */
    public static final int GAME_STATUS = 1;

    public static final int GAME_STATUS_DESC = 5;

    public static final int GAME_TEAM = 2;


    public static final int PRODUCT_MANAGER_NEED_GAME_CODE_STYLE = 6;

    public static final int GAME_CODE = 3;

    public static final int GAME_TIME = 4;


    public static List<Map<Object, String>> tomorrowNBAGame() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,1);
        String uri = api.replace("{date}", sdf.format(cal.getTime()));
        String json = "";

        try {
            json = NativeHttpClient.get(uri);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        NBADay nbaDay = gson.fromJson(json, NBADay.class);
        calendar.setTime(new Date());
   //     System.out.println(JsonUtil.toJson(nbaDay));
 //       System.out.println(nbaDay.payload.date.games.get(0).homeTeam.profile);
        sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
     //   System.out.println(sdf.format(nbaDay.timestamp));

        List<NBADay.Payload.Date.Game> xx = nbaDay.payload.date.games;
        List<Map<Object, String>> list = new ArrayList<Map<Object, String>>();
        for (int i = 0; i < xx.size(); i++) {
            Map<Object, String> map = new HashMap<>();
            map.put(GAME_TEAM, nbaDay.payload.date.games.get(i).homeTeam.profile.name + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name);
            map.put(GAME_CODE, nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore);
            map.put(PRODUCT_MANAGER_NEED_GAME_CODE_STYLE,nbaDay.payload.date.games.get(i).homeTeam.profile.name+ "(" +nbaDay.payload.date.games.get(i).boxscore.homeScore + ")" + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name + "(" + nbaDay.payload.date.games.get(i).boxscore.awayScore + ")");
            map.put(GAME_STATUS, nbaDay.payload.date.games.get(i).boxscore.status);
            map.put(GAME_TIME, sdf.format(nbaDay.payload.date.games.get(i).profile.utcMillis));

            if (null != nbaDay.payload.date.games.get(i).boxscore.statusDesc){
                map.put(GAME_STATUS_DESC, nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            }else{
                map.put(GAME_STATUS_DESC, "比赛未开始");
            }
            list.add(map);
/*
            System.out.println(nbaDay.payload.date.games.get(i).homeTeam.profile.name + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name + " 北京时间　" + sdf.format(nbaDay.payload.date.games.get(i).profile.utcMillis));
            if (!nbaDay.payload.date.games.get(i).boxscore.statusDesc.equals("3")) {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore + " " + nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            } else if (!nbaDay.payload.date.games.get(i).boxscore.statusDesc.equals("2")) {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.statusDesc);
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore);
            } else {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            }*/
        }
        return list;
    }


    public static List<Map<Object, String>> todayNBAGame() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String uri = api.replace("{date}", sdf.format(new Date()));

        String json = "";

        try {
            json = NativeHttpClient.get(uri);
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long s = calendar.getTimeInMillis();
        NBADay nbaDay = gson.fromJson(json, NBADay.class);
        calendar.setTime(new Date());
        long e = calendar.getTimeInMillis();
        //System.out.println(e - s);
        //System.out.println(JsonUtil.toJson(nbaDay));
        //System.out.println(nbaDay.payload.date.games.get(0).homeTeam.profile);
        sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        //System.out.println(sdf.format(nbaDay.timestamp));

        List<NBADay.Payload.Date.Game> xx = nbaDay.payload.date.games;
        List<Map<Object, String>> list = new ArrayList<Map<Object, String>>();
        for (int i = 0; i < xx.size(); i++) {
            Map<Object, String> map = new HashMap<>();
            map.put(GAME_TEAM, nbaDay.payload.date.games.get(i).homeTeam.profile.name + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name);
            map.put(GAME_CODE, nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore);
            map.put(GAME_STATUS, nbaDay.payload.date.games.get(i).boxscore.status);
            map.put(GAME_TIME, sdf.format(nbaDay.payload.date.games.get(i).profile.utcMillis));
            map.put(PRODUCT_MANAGER_NEED_GAME_CODE_STYLE,nbaDay.payload.date.games.get(i).homeTeam.profile.name+ "(" +nbaDay.payload.date.games.get(i).boxscore.homeScore + ")" + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name + "(" + nbaDay.payload.date.games.get(i).boxscore.awayScore + ")");
           // System.out.println( "开始时间" + "\t" + map.get(NBAUtils.GAME_TIME) + "\n" + "比赛队伍" +"\t" + map.get(NBAUtils.GAME_TEAM) +"\n" + "最终比分" +"\t"+ map.get(NBAUtils.GAME_CODE));
            if (null != nbaDay.payload.date.games.get(i).boxscore.statusDesc){
                map.put(GAME_STATUS_DESC, nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            }else{
                map.put(GAME_STATUS_DESC, "比赛未开始");
            }
            list.add(map);
/*
            System.out.println(nbaDay.payload.date.games.get(i).homeTeam.profile.name + " VS " + nbaDay.payload.date.games.get(i).awayTeam.profile.name + " 北京时间　" + sdf.format(nbaDay.payload.date.games.get(i).profile.utcMillis));
            if (!nbaDay.payload.date.games.get(i).boxscore.statusDesc.equals("3")) {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore + " " + nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            } else if (!nbaDay.payload.date.games.get(i).boxscore.statusDesc.equals("2")) {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.statusDesc);
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.homeScore + " VS " + nbaDay.payload.date.games.get(i).boxscore.awayScore);
            } else {
                System.out.println(nbaDay.payload.date.games.get(i).boxscore.statusDesc);
            }*/
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(todayNBAGame().get(0).get(PRODUCT_MANAGER_NEED_GAME_CODE_STYLE));
        //System.out.println(tomorrowNBAGame());

    }


    public class NBADay {
        public long timestamp;
        public Context context;
        private Error error;
        public Payload payload;

        public class Error {
            public String detail;
            public boolean isError;
            public String message;
        }

        public class Context {
            public User user;
            public Device device;

            public class User {
                public String countryCode;
                public String countryName;
                public String locale;
                public String timeZone;
                public String timeZoneCity;
            }

            public class Device {
                public String clazz;
            }
        }

        public class Payload {
            public Date date;
            public League league;
            public long nextAvailableDateMillis;
            public Season season;
            public long utcMillis;


            public class Season {
                public boolean isCurrent;
                public String rosterSeasonType;
                public String rosterSeasonYear;
                public String rosterSeasonYearDisplay;
                public String scheduleSeasonType;
                public String scheduleSeasonYear;
                public String scheduleYearDisplay;
                public String statsSeasonType;
                public String statsSeasonYear;
                public String statsSeasonYearDisplay;
                public String year;
                public String yearDisplay;
            }

            public class League {
                public String id;
                public String name;
            }

            public class Date {
                public long dateMillis;
                public String gameCount;
                public List<Game> games;

                public class Game {
                    public Profile profile;
                    public Boxscore boxscore;
                    public List<URL> urls;
                    public List<BroadCaster> broadcasters;
                    public HomeTeam homeTeam;
                    public AwayTeam awayTeam;

                    public class HomeTeam {
                        public Profile profile;
                        public Matchup matchup;
                        public Score score;
                        public PiontGameLeader piontGameLeader;
                        public AssistGameLeader assistGameLeader;
                        public ReboundGameLeader reboundGameLeader;


                        public class AssistGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }
                        }

                        // public
                        public class PiontGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }
                        }

                        public class ReboundGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }
                        }

                        public class Score {
                            public String assists;
                            public String biggestLead;
                            public String blocks;
                            public String blocksAgainst;
                            public String defRebs;
                            public String disqualifications;
                            public String ejections;
                            public String fastBreakPoints;
                            public String fga;
                            public String fgm;
                            public String fgpct;
                            public String flagrantFouls;
                            public String fouls;
                            public String fta;
                            public String ftm;
                            public String ftpct;
                            public String fullTimeoutsRemaining;
                            public String mins;
                            public String offRebs;
                            public String ot10Score;
                            public String ot1Score;
                            public String ot2Score;
                            public String ot3Score;
                            public String ot4Score;
                            public String ot5Score;
                            public String ot6Score;
                            public String ot7Score;
                            public String ot8Score;
                            public String ot9Score;
                            public String pointsInPaint;
                            public String pointsOffTurnovers;
                            public String q1Score;
                            public String q2Score;
                            public String q3Score;
                            public String q4Score;
                            public String rebs;
                            public String score;
                            public String seconds;
                            public String shortTimeoutsRemaining;
                            public String steals;
                            public String technicalFouls;
                            public String tpa;
                            public String tpm;
                            public String tppct;
                            public String turnovers;
                        }

                        public class Matchup {
                            public String confRank;
                            public String divRank;
                            public int losses;
                            public String seriesText;
                            public int wins;
                        }

                        public class Profile {
                            public String abbr;
                            public String city;
                            public String cityEn;
                            public String code;
                            public String conference;
                            public String displayAbbr;
                            public String displayConference;
                            public String division;
                            public String id;
                            public boolean isAllStarTeam;
                            public boolean isLeagueTeam;
                            public String name;
                            public String nameEn;
                        }
                    }

                    public class AwayTeam {
                        public Prefile profile;
                        public Matchup matchup;
                        public Score score;
                        public PiontGameLeader piontGameLeader;
                        public AssistGameLeader assistGameLeader;
                        public ReboundGameLeader reboundGameLeader;


                        public class AssistGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }
                        }

                        // public
                        public class PiontGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }
                        }

                        public class ReboundGameLeader {
                            public Profile profile;
                            public StatTotal statTotal;

                            public class StatTotal {
                                public String assists;
                                public String blocks;
                                public String defRebs;
                                public String fga;
                                public String fgm;
                                public String fgpct;
                                public String fouls;
                                public String fta;
                                public String ftm;
                                public String ftpct;
                                public String mins;
                                public String offReds;
                                public String points;
                                public String rebs;
                                public String secs;
                                public String steals;
                                public String tpa;
                                public String tpm;
                                public String tppct;
                                public String turnovers;

                            }

                            public class Profile {
                                public String code;
                                public String country;
                                public String displayAffiliaion;
                                public String displayName;
                                public String displayNameEn;
                                public String dob;
                                public String draftYear;
                                public String experience;
                                public String firstInitial;
                                public String firstName;
                                public String firstNameEn;
                                public String height;
                                public String jerseyNo;
                                public String lastName;
                                public String lastNameEn;
                                public String playerId;
                                public String position;
                                public String schoolType;
                                public String weight;
                            }
                        }

                        public class Score {
                            public String assists;
                            public String biggestLead;
                            public String blocks;
                            public String blocksAgainst;
                            public String defRebs;
                            public String disqualifications;
                            public String ejections;
                            public String fastBreakPoints;
                            public String fga;
                            public String fgm;
                            public String fgpct;
                            public String flagrantFouls;
                            public String fouls;
                            public String fta;
                            public String ftm;
                            public String ftpct;
                            public String fullTimeoutsRemaining;
                            public String mins;
                            public String offRebs;
                            public String ot10Score;
                            public String ot1Score;
                            public String ot2Score;
                            public String ot3Score;
                            public String ot4Score;
                            public String ot5Score;
                            public String ot6Score;
                            public String ot7Score;
                            public String ot8Score;
                            public String ot9Score;
                            public String pointsInPaint;
                            public String pointsOffTurnovers;
                            public String q1Score;
                            public String q2Score;
                            public String q3Score;
                            public String q4Score;
                            public String rebs;
                            public String score;
                            public String seconds;
                            public String shortTimeoutsRemaining;
                            public String steals;
                            public String technicalFouls;
                            public String tpa;
                            public String tpm;
                            public String tppct;
                            public String turnovers;
                        }

                        public class Matchup {
                            public String confRank;
                            public String divRank;
                            public int losses;
                            public String seriesText;
                            public int wins;
                        }

                        public class Prefile {
                            public String abbr;
                            public String city;
                            public String cityEn;
                            public String code;
                            public String conference;
                            public String displayAbbr;
                            public String displayConference;
                            public String division;
                            public String id;
                            public boolean isAllStarTeam;
                            public boolean isLeagueTeam;
                            public String name;
                            public String nameEn;
                        }
                    }

                    public class BroadCaster {
                        public String id;
                        public String media;
                        public String name;
                        public String range;
                        public String type;

                    }

                    public class URL {
                        public String displayText;
                        public String type;
                        public String value;

                    }

                    public class Boxscore {
                        public String attendance;
                        public String awayScore;
                        public String gameLength;
                        public String homeScore;
                        public String officialsDisplayName1;
                        public String officialsDisplayName2;
                        public String officialsDisplayName3;
                        public String period;
                        public String periodClock;
                        public String status;
                        public String statusDesc;
                        public String ties;


                    }

                    public class Profile {
                        public String arenaName;
                        public String awayTeamId;
                        public String gameId;
                        public String homeTeamId;
                        public String number;
                        public String seasonType;
                        public String sequence;
                        public long utcMillis;
                    }
                }
            }
        }
    }

}
