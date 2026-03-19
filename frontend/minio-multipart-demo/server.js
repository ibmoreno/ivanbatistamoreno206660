const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = Number(process.env.PORT || 3000);
const PUBLIC_DIR = path.join(__dirname, "public");

const CONTENT_TYPES = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "application/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".svg": "image/svg+xml"
};

function resolveFile(urlPath) {
  const requestPath = urlPath === "/" ? "/index.html" : urlPath;
  const safePath = path.normalize(requestPath).replace(/^(\.\.[/\\])+/, "");
  return path.join(PUBLIC_DIR, safePath);
}

const server = http.createServer((req, res) => {
  const filePath = resolveFile(req.url.split("?")[0]);

  if (!filePath.startsWith(PUBLIC_DIR)) {
    res.writeHead(403, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("Forbidden");
    return;
  }

  fs.readFile(filePath, (error, content) => {
    if (error) {
      res.writeHead(error.code === "ENOENT" ? 404 : 500, {
        "Content-Type": "text/plain; charset=utf-8"
      });
      res.end(error.code === "ENOENT" ? "Not Found" : "Internal Server Error");
      return;
    }

    const extension = path.extname(filePath).toLowerCase();
    res.writeHead(200, {
      "Content-Type": CONTENT_TYPES[extension] || "application/octet-stream",
      "Cache-Control": "no-store"
    });
    res.end(content);
  });
});

server.listen(PORT, () => {
  console.log(`Demo disponível em http://localhost:${PORT}`);
});
