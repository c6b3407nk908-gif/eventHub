package com.eventmate.config;

import com.eventmate.model.Event;
import com.eventmate.model.EventCategory;
import com.eventmate.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;

    public DatabaseSeeder(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() == 0) {
            System.out.println("No events found in the database. Seeding sample events...");

            // Event 1: Grand Music Festival
            Event event1 = new Event();
            event1.setEventName("Grand Music Festival 2026");
            event1.setEventType("music");
            event1.setDescription("Experience the best music festival of the summer with live performances from top artists and bands. Free food and drinks will be provided.");
            event1.setCategory(EventCategory.FESTIVAL);
            event1.setEventTime("18:00");
            event1.setLocation("ATHIRAMPUZHA, Kottayam");
            event1.setImageUrl("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800");
            event1.setJobVacancy(true);
            event1.setAdsOpportunity(true);
            event1.setVerified(true);
            event1.setOrganizerId("seeder-admin");
            
            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.DAY_OF_YEAR, 5); // 5 days from now
            event1.setEventDate(cal1.getTime());

            // Event 2: Kerala Tech Hackathon
            Event event2 = new Event();
            event2.setEventName("Kerala Tech Hackathon");
            event2.setEventType("tech");
            event2.setDescription("Join 24 hours of non-stop coding, innovation, and fun with developers from all over the state. Exciting prizes for winners.");
            event2.setCategory(EventCategory.HACKATHON);
            event2.setEventTime("09:00");
            event2.setLocation("ERNAKULAM, Ernakulam");
            event2.setImageUrl("https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800");
            event2.setJobVacancy(false);
            event2.setAdsOpportunity(true);
            event2.setVerified(true);
            event2.setOrganizerId("seeder-admin");

            Calendar cal2 = Calendar.getInstance();
            cal2.add(Calendar.DAY_OF_YEAR, 12); // 12 days from now
            event2.setEventDate(cal2.getTime());

            // Event 3: Standup Comedy Night
            Event event3 = new Event();
            event3.setEventName("Standup Comedy Night");
            event3.setEventType("comedy");
            event3.setDescription("Get ready to laugh out loud with the funniest comedians in town performing live in Kottayam.");
            event3.setCategory(EventCategory.OTHER);
            event3.setEventTime("20:00");
            event3.setLocation("ATHIRAMPUZHA, Kottayam");
            event3.setImageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eed262?w=800");
            event3.setJobVacancy(true);
            event3.setAdsOpportunity(false);
            event3.setVerified(false);
            event3.setOrganizerId("seeder-admin");

            Calendar cal3 = Calendar.getInstance();
            cal3.add(Calendar.DAY_OF_YEAR, 2); // 2 days from now
            event3.setEventDate(cal3.getTime());

            eventRepository.saveAll(List.of(event1, event2, event3));
            System.out.println("Successfully seeded 3 sample events into the database.");
        } else {
            System.out.println("Events already exist in the database. Skipping seeding.");
        }
    }
}
