package com.hungnln.vleague.response;

import com.hungnln.vleague.constant.activity.ClubStandingRole;
import com.hungnln.vleague.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TournamentStandingResponse {
    Club club;
    int mp;
    int w;
    int d;
    int l;
    int gf;
    int ga;
    int gd;
    int pts;
    List<ClubStandingRole> last5;

    public void addToLast5(ClubStandingRole role) {
        if (last5 == null) {
            last5 = new ArrayList<>();
        } else if (last5.size()==5) {
            last5.remove(4);
        }
            last5.add(0,role);

    }
    public TournamentStandingResponse(Club club) {
        this.club = club;
        this.mp = 0;
        this.w = 0;
        this.d = 0;
        this.l = 0;
        this.gf = 0;
        this.ga = 0;
        this.gd = 0;
        this.pts = 0;
        this.last5 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            this.last5.add(ClubStandingRole.NOT_PLAYED);
        }
    }
}
