-- inserção de artistas, albuns e artistas x albuns
WITH artistas AS (
INSERT INTO artista (nome)
VALUES
    ('Serj Tankian'),
    ('Mike Shinoda'),
    ('Michel Teló'),
    ('Guns N’ Roses')
    RETURNING id, nome
    ), albuns AS (
INSERT INTO album (titulo)
VALUES
    ('Harakiri'),
    ('Black Blooms'),
    ('The Rough Dog'),
    ('The Rising Tied'),
    ('Post Traumatic'),
    ('Post Traumatic EP'),
    ('Where’d You Go'),
    ('Bem Sertanejo'),
    ('Bem Sertanejo - O Show (Ao Vivo)'),
    ('Bem Sertanejo - (1ª Temporada) - EP'),
    ('Use Your Illusion I'),
    ('Use Your Illusion II'),
    ('Greatest Hits')
    RETURNING id, titulo
    )

-- NOTA: inserção sem depender da ordem dos IDs gerados para artistas e albuns
INSERT INTO artista_album (id_artista, id_album)
SELECT a.id, al.id
FROM artistas a
         JOIN albuns al ON
    (
        (a.nome = 'Serj Tankian' AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog'))
            OR (a.nome = 'Mike Shinoda' AND al.titulo IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where’d You Go'))
            OR (a.nome = 'Michel Teló' AND al.titulo IN ('Bem Sertanejo', 'Bem Sertanejo - O Show (Ao Vivo)', 'Bem Sertanejo - (1ª Temporada) - EP'))
            OR (a.nome = 'Guns N’ Roses' AND al.titulo IN ('Use Your Illusion I', 'Use Your Illusion II', 'Greatest Hits'))
        );
