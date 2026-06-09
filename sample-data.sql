-- Sample Data for Snapora
USE snapora;

-- Passwords are BCrypt of "password123"
INSERT INTO users (username, email, password_hash, full_name, bio) VALUES
('alex_dev', 'alex@snapora.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iK0i', 'Alex Johnson', 'Software developer & photographer 📸'),
('sara_design', 'sara@snapora.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iK0i', 'Sara Williams', 'UI/UX Designer | Coffee lover ☕'),
('mike_travel', 'mike@snapora.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iK0i', 'Mike Chen', 'Wanderlust traveler 🌍 | Capturing moments');

-- Sample posts (image_url points to placeholder)
INSERT INTO posts (user_id, caption, image_url, location) VALUES
(1, 'Building something amazing today 🚀 #coding #developer', 'https://picsum.photos/seed/post1/600/600', 'San Francisco, CA'),
(2, 'New design system in progress ✨ #uidesign #figma', 'https://picsum.photos/seed/post2/600/600', 'New York, NY'),
(3, 'Sunset from the top of the world 🌅 #travel #photography', 'https://picsum.photos/seed/post3/600/600', 'Santorini, Greece'),
(1, 'Late night debugging sessions hit different 💻☕', 'https://picsum.photos/seed/post4/600/600', NULL),
(2, 'Color palette exploration for the new brand 🎨', 'https://picsum.photos/seed/post5/600/600', 'Brooklyn, NY'),
(3, 'Morning hike before the crowds arrive 🏔️ #hiking', 'https://picsum.photos/seed/post6/600/600', 'Swiss Alps');

-- Follows
INSERT INTO follows (follower_id, following_id) VALUES
(1, 2), (1, 3), (2, 1), (2, 3), (3, 1);

-- Likes
INSERT INTO post_likes (post_id, user_id) VALUES
(1, 2), (1, 3), (2, 1), (2, 3), (3, 1), (3, 2), (4, 2), (5, 1), (6, 1), (6, 2);

-- Comments
INSERT INTO comments (post_id, user_id, content) VALUES
(1, 2, 'This looks incredible! What stack are you using? 🔥'),
(1, 3, 'Keep it up! Can''t wait to see the final product'),
(2, 1, 'Love the color choices! Very clean 👌'),
(3, 1, 'Absolutely breathtaking! Adding this to my bucket list 😍'),
(3, 2, 'The lighting is perfect! What camera do you use?');

-- Replies
INSERT INTO comments (post_id, user_id, content, parent_id) VALUES
(1, 1, 'Spring Boot + React! Full details coming soon 😄', 1),
(3, 3, 'Thank you! Shot on Sony A7III 📷', 5);
