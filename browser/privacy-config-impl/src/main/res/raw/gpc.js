

(function() {
    if (navigator.globalPrivacyControl === undefined) {
        Object.defineProperty(Navigator.prototype, 'globalPrivacyControl', {
            get: () => true,
            configurable: true,
            enumerable: true
        });
    } else {
        try {
            navigator.globalPrivacyControl = true;
        } catch (e) {
            console.error('globalPrivacyControl is not writable: ', e);
        }
    }
})();
