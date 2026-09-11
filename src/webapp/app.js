// GlobalDocs Solutions -- Document Processing System client.
// Talks directly to the Java backend (com.sun.net.httpserver) that serves this page.
// No document-type/format filtering happens here: every upload is sent as-is and the
// backend is the sole authority on whether it is accepted.

(function () {
    "use strict";

    let metadata = null;
    let batchRowCount = 0;

    document.addEventListener("DOMContentLoaded", init);

    function init() {
        wireTabs();
        wireBatchControls();
        document.getElementById("single-form").addEventListener("submit", onSingleSubmit);
        document.getElementById("batch-form").addEventListener("submit", onBatchSubmit);
        loadMetadata();
    }

    function wireTabs() {
        document.querySelectorAll(".tab-btn").forEach(function (btn) {
            btn.addEventListener("click", function () {
                document.querySelectorAll(".tab-btn").forEach(function (b) {
                    b.classList.remove("active");
                    b.setAttribute("aria-selected", "false");
                });
                document.querySelectorAll(".tab-panel").forEach(function (p) {
                    p.classList.remove("active");
                });
                btn.classList.add("active");
                btn.setAttribute("aria-selected", "true");
                document.getElementById("tab-" + btn.dataset.tab).classList.add("active");
            });
        });
    }

    function setServerStatus(state, text) {
        const pill = document.getElementById("server-status");
        pill.className = "status-pill status-pill--" + state;
        pill.textContent = text;
    }

    function loadMetadata() {
        setServerStatus("pending", "Connecting to backend...");
        fetch("/api/metadata")
            .then(function (res) {
                if (!res.ok) throw new Error("HTTP " + res.status);
                return res.json();
            })
            .then(function (data) {
                metadata = data;
                setServerStatus("ok", "Backend connected");
                populateCountrySelect(document.getElementById("single-country"));
                populateCountrySelect(document.getElementById("batch-country"));
                populateDocTypeSelect(document.getElementById("single-doctype"));
                document.getElementById("single-country").addEventListener("change", refreshSingleHints);
                document.getElementById("single-doctype").addEventListener("change", refreshSingleHints);
                refreshSingleHints();
                addBatchRow();
            })
            .catch(function (err) {
                setServerStatus("error", "Backend unreachable");
                console.error("Failed to load /api/metadata", err);
            });
    }

    function populateCountrySelect(select) {
        select.innerHTML = "";
        metadata.countries.forEach(function (country) {
            const option = document.createElement("option");
            option.value = country.code;
            option.textContent = country.label + " (" + country.isoCode + ")";
            select.appendChild(option);
        });
    }

    function populateDocTypeSelect(select) {
        select.innerHTML = "";
        metadata.documentTypes.forEach(function (type) {
            const option = document.createElement("option");
            option.value = type.code;
            option.textContent = type.label;
            select.appendChild(option);
        });
    }

    function findCountry(code) {
        return metadata.countries.find(function (c) { return c.code === code; });
    }

    function refreshSingleHints() {
        const country = findCountry(document.getElementById("single-country").value);
        const docTypeSelect = document.getElementById("single-doctype");
        const docTypeHint = document.getElementById("single-doctype-hint");
        const taxIdHint = document.getElementById("single-taxid-hint");
        if (!country) return;

        taxIdHint.textContent = "Format for " + country.label + ": " + country.taxIdFormat;

        const supported = country.supportedDocumentTypes.indexOf(docTypeSelect.value) !== -1;
        if (!supported) {
            docTypeHint.textContent = country.label + " does not accept this document type through this platform. It will be rejected by the server.";
            docTypeHint.classList.add("warn");
        } else {
            docTypeHint.textContent = "Max file size for " + country.label + ": " + formatBytes(country.maxFileSizeBytes);
            docTypeHint.classList.remove("warn");
        }
    }

    function formatBytes(bytes) {
        if (bytes >= 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + " MB";
        if (bytes >= 1024) return (bytes / 1024).toFixed(1) + " KB";
        return bytes + " B";
    }

    // ---------- Single document ----------

    function onSingleSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const fileInput = document.getElementById("single-file");
        if (!fileInput.files.length) {
            renderSingleResult(null, "Please choose a file first.");
            return;
        }

        const formData = new FormData();
        formData.append("countryCode", document.getElementById("single-country").value);
        formData.append("documentType", document.getElementById("single-doctype").value);
        formData.append("taxId", document.getElementById("single-taxid").value || "");
        formData.append("file", fileInput.files[0]);

        setSubmitting(form, true);
        fetch("/api/documents/process", { method: "POST", body: formData })
            .then(function (res) {
                return res.json().then(function (body) { return { ok: res.ok, body: body }; });
            })
            .then(function (wrapped) {
                if (!wrapped.ok) {
                    renderSingleResult(null, wrapped.body.error || "Request failed.");
                    return;
                }
                renderSingleResult(wrapped.body, null);
            })
            .catch(function (err) {
                renderSingleResult(null, "Network error: " + err.message);
            })
            .finally(function () {
                setSubmitting(form, false);
            });
    }

    function setSubmitting(form, isSubmitting) {
        const button = form.querySelector("button[type=submit]");
        button.disabled = isSubmitting;
        button.querySelector(".spinner").hidden = !isSubmitting;
    }

    function renderSingleResult(result, errorText) {
        const container = document.getElementById("single-result");
        container.innerHTML = "";
        if (errorText) {
            const div = document.createElement("div");
            div.className = "result-block FAILED";
            div.textContent = errorText;
            container.appendChild(div);
            return;
        }
        container.appendChild(buildResultBlock(result));
    }

    function buildResultBlock(result) {
        const block = document.createElement("div");
        block.className = "result-block " + result.status;

        const badge = document.createElement("span");
        badge.className = "result-badge";
        badge.textContent = result.status;
        block.appendChild(badge);

        const filename = document.createElement("div");
        filename.className = "result-filename";
        filename.textContent = result.fileName;
        block.appendChild(filename);

        const message = document.createElement("div");
        message.className = "result-message";
        message.textContent = result.message;
        block.appendChild(message);

        const keys = Object.keys(result.metadata || {});
        if (keys.length > 0) {
            const table = document.createElement("table");
            table.className = "meta-table";
            keys.forEach(function (key) {
                const row = document.createElement("tr");
                const keyCell = document.createElement("td");
                keyCell.textContent = key;
                const valCell = document.createElement("td");
                valCell.textContent = result.metadata[key];
                row.appendChild(keyCell);
                row.appendChild(valCell);
                table.appendChild(row);
            });
            block.appendChild(table);
        }
        return block;
    }

    // ---------- Batch ----------

    function wireBatchControls() {
        document.getElementById("batch-add-row").addEventListener("click", addBatchRow);
    }

    function addBatchRow() {
        if (!metadata) return;
        batchRowCount += 1;
        const rowId = "row-" + batchRowCount;

        const row = document.createElement("div");
        row.className = "batch-row";
        row.dataset.rowId = rowId;

        const select = document.createElement("select");
        metadata.documentTypes.forEach(function (type) {
            const option = document.createElement("option");
            option.value = type.code;
            option.textContent = type.label;
            select.appendChild(option);
        });

        const fileInput = document.createElement("input");
        fileInput.type = "file";
        fileInput.required = true;

        const removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.className = "row-remove";
        removeBtn.textContent = "Remove";
        removeBtn.addEventListener("click", function () {
            row.remove();
        });

        row.appendChild(select);
        row.appendChild(fileInput);
        row.appendChild(removeBtn);
        document.getElementById("batch-rows").appendChild(row);
    }

    function onBatchSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const rows = Array.from(document.querySelectorAll("#batch-rows .batch-row"));
        if (rows.length === 0) {
            renderBatchResult(null, "Add at least one file to the batch.");
            return;
        }

        const formData = new FormData();
        formData.append("countryCode", document.getElementById("batch-country").value);
        formData.append("taxId", document.getElementById("batch-taxid").value || "");

        let missingFile = false;
        rows.forEach(function (row) {
            const select = row.querySelector("select");
            const fileInput = row.querySelector("input[type=file]");
            if (!fileInput.files.length) {
                missingFile = true;
                return;
            }
            formData.append("documentType", select.value);
            formData.append("file", fileInput.files[0]);
        });

        if (missingFile) {
            renderBatchResult(null, "Every row needs a file selected.");
            return;
        }

        setSubmitting(form, true);
        fetch("/api/documents/batch", { method: "POST", body: formData })
            .then(function (res) {
                return res.json().then(function (body) { return { ok: res.ok, body: body }; });
            })
            .then(function (wrapped) {
                if (!wrapped.ok) {
                    renderBatchResult(null, wrapped.body.error || "Request failed.");
                    return;
                }
                renderBatchResult(wrapped.body, null);
            })
            .catch(function (err) {
                renderBatchResult(null, "Network error: " + err.message);
            })
            .finally(function () {
                setSubmitting(form, false);
            });
    }

    function renderBatchResult(batchResult, errorText) {
        const summary = document.getElementById("batch-summary");
        const container = document.getElementById("batch-result");
        container.innerHTML = "";

        if (errorText) {
            summary.hidden = true;
            const div = document.createElement("div");
            div.className = "result-block FAILED";
            div.textContent = errorText;
            container.appendChild(div);
            return;
        }

        summary.hidden = false;
        summary.innerHTML = "";
        [
            ["Total", batchResult.totalCount, ""],
            ["Success", batchResult.successCount, "SUCCESS"],
            ["Rejected", batchResult.rejectedCount, "REJECTED"],
            ["Failed", batchResult.failedCount, "FAILED"]
        ].forEach(function (entry) {
            const chip = document.createElement("span");
            chip.className = "summary-chip " + entry[2];
            chip.textContent = entry[0] + ": " + entry[1];
            summary.appendChild(chip);
        });

        const list = document.createElement("div");
        list.className = "batch-result-list";
        batchResult.results.forEach(function (result) {
            list.appendChild(buildResultBlock(result));
        });
        container.appendChild(list);
    }
})();
