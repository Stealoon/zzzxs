// Service Worker disabled for study room system - no caching
self.addEventListener('install', function(e) { self.skipWaiting(); });
self.addEventListener('activate', function(e) {
    e.waitUntil(
        caches.keys().then(function(cacheNames) {
            return Promise.all(
                cacheNames.map(function(cacheName) {
                    return caches.delete(cacheName);
                })
            );
        }).then(function() {
            return self.clients.claim();
        })
    );
});
self.addEventListener('fetch', function(e) {
    // No caching - always fetch from network
});
