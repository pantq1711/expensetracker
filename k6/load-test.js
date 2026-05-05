import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

const BASE_URL = 'http://localhost:8080';

// Hàm setup chạy duy nhất 1 lần đầu tiên
export function setup() {
    // 1. Login lấy Token
    const loginRes = http.post(`${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: 'test@example11.com', password: 'password123' }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    const token = loginRes.json('token');

    // 2. Tạo ngay 1 cái Wallet mới để test
    const createWalletRes = http.post(`${BASE_URL}/api/wallets`,
        JSON.stringify({
            name: "Ví Test K6 Auto",
            budget: 50000000 // Setup budget thoải mái
        }),
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        }
    );
    const walletId = createWalletRes.json('id'); // Bắt lấy ID của ví vừa tạo

    // 3. Trả về cả token và walletId cho các VUs bên dưới dùng chung
    return {
        token: token,
        walletId: walletId
    };
}

// Hàm này sẽ được 100 VUs chạy lặp đi lặp lại
export default function (data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.token}`,
    };

    // ==============================================================
    // BÀI TEST 1: ATOMIC UPDATE (Dùng walletId vừa tạo ở trên)
    // ==============================================================
    const depositRes = http.post(`${BASE_URL}/api/wallets/${data.walletId}/deposit`,
        JSON.stringify({ amount: 1000 }),
        { headers: headers }
    );

    check(depositRes, {
        'deposit 200': (r) => r.status === 200,
    });

    // ==============================================================
    // BÀI TEST 2: IDEMPOTENCY KEY
    // ==============================================================
    const idempotencyKey = `k6-idemp-${__VU}-${__ITER}`;

    const txPayload = JSON.stringify({
        amount: 50000,
        date: '2026-05-03',
        type: 'EXPENSE',
        note: `k6 test iter ${__ITER}`,
        categoryId: 6, // Vẫn dùng ID Category hợp lệ của user này
    });

    const req1 = http.post(`${BASE_URL}/api/transactions`, txPayload, {
        headers: { ...headers, 'Idempotency-Key': idempotencyKey }
    });
    check(req1, { 'tx created 201 (First Try)': (r) => r.status === 201 });

    const req2 = http.post(`${BASE_URL}/api/transactions`, txPayload, {
        headers: { ...headers, 'Idempotency-Key': idempotencyKey }
    });
    check(req2, { 'tx duplicated 201 (Cached)': (r) => r.status === 201 });

    sleep(1);
}