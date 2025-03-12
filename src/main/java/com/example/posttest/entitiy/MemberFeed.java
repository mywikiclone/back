package com.example.posttest.entitiy;


import com.example.posttest.etc.FeedEnum;
import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

@Entity
@Getter
@NoArgsConstructor
public class MemberFeed {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long feed_id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="topic_id")
    private DiscussionTopic discussionTopic;


    @Column(name="feed_check")
    @Enumerated(EnumType.STRING)
    private FeedEnum feedEnum;


    public MemberFeed(Member member, DiscussionTopic discussionTopic, FeedEnum feedEnum) {
        this.member = member;
        this.discussionTopic = discussionTopic;
        this.feedEnum = feedEnum;
    }
}
