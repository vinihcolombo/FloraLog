function showConfirmModal(mensagem) {
    return new Promise((resolve) => {

        const existingModal = document.getElementById('customConfirmModal');
        if (existingModal) existingModal.remove();

        const modalHtml = `
            <div class="modal fade" id="customConfirmModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content bg-flora-card border-flora-green text-body shadow-lg" style="border: 1px solid var(--border-color);">
                        <div class="modal-header border-bottom border-flora-green" style="border-color: var(--border-color) !important;">
                            <h5 class="modal-title text-flora-green fw-bold">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i> Confirmação
                            </h5>
                            <button type="button" class="btn-close filter-invert" data-bs-dismiss="modal" aria-label="Fechar"></button>
                        </div>
                        <div class="modal-body py-4">
                            <p class="mb-0 fs-6">${mensagem}</p>
                        </div>
                        <div class="modal-footer border-top border-flora-green" style="border-color: var(--border-color) !important;">
                            <button type="button" class="btn btn-outline-secondary btn-sm px-3" data-bs-dismiss="modal" id="cancelBtn">Cancelar</button>
                            <button type="button" class="btn btn-flora-green btn-sm px-3" id="confirmBtn">Confirmar</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);

        const modalElement = document.getElementById('customConfirmModal');
        const bsModal = new bootstrap.Modal(modalElement);
        bsModal.show();

        const confirmBtn = document.getElementById('confirmBtn');
        const cancelBtn = document.getElementById('cancelBtn');

        let result = false;

        confirmBtn.addEventListener('click', () => {
            result = true;
            bsModal.hide();
        });

        modalElement.addEventListener('hidden.bs.modal', () => {
            modalElement.remove();
            resolve(result);
        });
    });
}