INSERT INTO region
VALUES (1, 11),
       (2, 12),
       (3, 13),
       (4, 14),
       (5, 15);

INSERT INTO time_period
VALUES (1, '07:00', '10:00'),
       (2, '10:00', '13:00'),
       (3, '13:00', '16:00'),
       (4, '16:00', '19:00'),
       (5, '19:00', '22:00'),
       (6, '07:00', '22:00'),
       (7, '10:00', '19:00');

INSERT INTO courier
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 3),
       (5, 2),
       (6, 3),
       (7, 3),
       (8, 2),
       (9, 1);

INSERT INTO courier_region
VALUES (1, 1), (1, 2), (1, 3),
       (2, 3), (2, 4), (2, 5),
       (3, 2), (3, 4),
       (4, 2), (4, 1), (4, 4), (4, 5), (4, 3),
       (5, 1),
       (6, 2),
       (7, 3),
       (8, 4),
       (9, 5);

INSERT INTO courier_time_period
VALUES (1, 1), (1, 3), (1, 5),
       (2, 2), (2, 4),
       (3, 1), (3, 5),
       (4, 6),
       (5, 7),
       (6, 4),
       (7, 5),
       (8, 3),
       (9, 6);