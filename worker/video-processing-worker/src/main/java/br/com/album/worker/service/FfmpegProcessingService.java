package br.com.album.worker.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FfmpegProcessingService {

    @Value("${video-processing.preview-duration-seconds:10}")
    private Integer previewDurationSeconds;

    @Value("${video-processing.thumbnail-second:5}")
    private Integer thumbnailSecond;

    @Value("${video-processing.segment-duration-seconds:4}")
    private Integer segmentDurationSeconds;

    public long probeDurationSeconds(Path inputFile) throws Exception {
        Process process = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                inputFile.toString()
        ).start();

        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.readLine();
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || output == null || output.isBlank()) {
            throw new IllegalStateException("Nao foi possivel obter a duracao do video via ffprobe");
        }

        double duration = Double.parseDouble(output.trim());
        return Math.max(1L, Math.round(duration));
    }

    public void generateThumbnail(Path inputFile, Path outputFile) throws Exception {
        runCommand(List.of(
                "ffmpeg",
                "-y",
                "-ss", String.valueOf(thumbnailSecond),
                "-i", inputFile.toString(),
                "-frames:v", "1",
                "-q:v", "2",
                outputFile.toString()
        ));
    }

    public void generateAudioTrack(Path inputFile, Path outputFile) throws Exception {
        runCommand(List.of(
                "ffmpeg",
                "-y",
                "-i", inputFile.toString(),
                "-vn",
                "-c:a", "aac",
                "-b:a", "128k",
                outputFile.toString()
        ));
    }

    public void generatePreview(Path inputFile, Path outputFile) throws Exception {
        runCommand(List.of(
                "ffmpeg",
                "-y",
                "-i", inputFile.toString(),
                "-t", String.valueOf(previewDurationSeconds),
                "-vf", "scale='min(854,iw)':-2",
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-movflags", "+faststart",
                "-pix_fmt", "yuv420p",
                "-c:a", "aac",
                outputFile.toString()
        ));
    }

    public void generate360p(Path inputFile, Path outputFile) throws Exception {
        generateScaledVersion(inputFile, outputFile, 640, 360, "900k", "1200k");
    }

    public void generate720p(Path inputFile, Path outputFile) throws Exception {
        generateScaledVersion(inputFile, outputFile, 1280, 720, "2800k", "2996k");
    }

    public void generateDash(Path rendition360File, Path rendition720File, Path audioFile, Path outputDirectory) throws Exception {
        Path manifestFile = outputDirectory.resolve("manifest.mpd");
        runCommand(List.of(
                "ffmpeg",
                "-y",
                "-i", rendition360File.toString(),
                "-i", rendition720File.toString(),
                "-i", audioFile.toString(),
                "-map", "0:v:0",
                "-map", "1:v:0",
                "-map", "2:a:0",
                "-c:v", "copy",
                "-c:a", "copy",
                "-seg_duration", String.valueOf(segmentDurationSeconds),
                "-frag_type", "every_frame",
                "-streaming", "1",
                "-remove_at_exit", "0",
                "-ldash", "0",
                "-window_size", "5",
                "-extra_window_size", "5",
                "-f", "dash",
                "-use_template", "1",
                "-use_timeline", "1",
                "-init_seg_name", "init-$RepresentationID$.m4s",
                "-media_seg_name", "chunk-$RepresentationID$-$Number%05d$.m4s",
                "-adaptation_sets", "id=0,streams=v id=1,streams=a",
                manifestFile.toString()
        ));
    }

    private void generateScaledVersion(Path inputFile,
                                       Path outputFile,
                                       int width,
                                       int height,
                                       String bitrate,
                                       String maxRate) throws Exception {
        runCommand(List.of(
                "ffmpeg",
                "-y",
                "-i", inputFile.toString(),
                "-vf", "scale=" + width + ":" + height + ":force_original_aspect_ratio=decrease,pad=" + width + ":" + height + ":(ow-iw)/2:(oh-ih)/2",
                "-c:v", "libx264",
                "-b:v", bitrate,
                "-maxrate", maxRate,
                "-bufsize", maxRate,
                "-preset", "veryfast",
                "-pix_fmt", "yuv420p",
                "-profile:v", "main",
                "-movflags", "+faststart",
                "-g", "48",
                "-keyint_min", "48",
                "-sc_threshold", "0",
                "-an",
                outputFile.toString()
        ));
    }

    private void runCommand(List<String> command) throws Exception {
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Comando FFmpeg falhou com codigo %d. Saida: %s", exitCode, output));
        }
        log.info("FFmpeg executado com sucesso: {}", command.getFirst());
    }
}
