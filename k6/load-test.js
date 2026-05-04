import http from 'k6/http';
import { check, sleep } from 'k6';

// Config: 100 user đồng thời trong 30 giây
export const options = {
    vus: 100,
    duration: '30s',
    thresholds: {
        http_req_duration: ['p(95)<250'], // 95% request phải dưới 250
        http_req_failed: ['rate<0.01'],   // Error rate dưới 1%
    },
};

const BASE_URL = 'http://localhost:8080';

// Lấy token trước khi test — chạy 1 lần
export function setup() {
    const loginRes = http.post(`${BASE_URL}/api/auth/login`,
        JSON.stringify({ email: 'test@example11.com', password: 'password123' }),
        { headers: { 'Content-Type': 'application/json' } }
    );
    return { token: loginRes.json('token') };
}

export default function (data) {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.token}`,
    };

    // Test 1: GET transactions (có cache Redis)
    const listRes = http.get(`${BASE_URL}/api/transactions?page=0&size=10`, { headers });
    check(listRes, {
        'transaction list 200': (r) => r.status === 200,
    });

    sleep(0.5);

    // Test 2: GET report/summary (có cache Redis)
    const summaryRes = http.get(`${BASE_URL}/api/reports/summary`, { headers });
    check(summaryRes, {
        'summary 200': (r) => r.status === 200,
    });

    sleep(0.5);

    // Test 3: POST transaction với Idempotency-Key
    const idempotencyKey = `test-${__VU}-${__ITER}`; // unique per virtual user + iteration
    const createRes = http.post(`${BASE_URL}/api/transactions`,
        JSON.stringify({
            amount: 50000,
            date: '2026-05-03',
            type: 'EXPENSE',
            note: 'k6 test',
            categoryId: 6,
        }),
        { headers: { ...headers, 'Idempotency-Key': idempotencyKey } }
    );
    check(createRes, {
        'create transaction 201': (r) => r.status === 201,
    });

    sleep(1);
}