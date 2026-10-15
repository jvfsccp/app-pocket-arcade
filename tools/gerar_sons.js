// Gera os efeitos sonoros do Pocket Arcade (WAV 16 bits, mono, 22050 Hz).
// Sons sintetizados por código: originais, sem direitos autorais.
// Uso: node tools/gerar_sons.js
const fs = require('fs');
const path = require('path');

const TAXA = 22050;
const DESTINO = path.join(__dirname, '..', 'app', 'src', 'main', 'res', 'raw');

// Gera amostras de uma nota. forma: 'quadrada' | 'seno' | 'triangular'
function nota(freq, duracaoMs, forma, volume, freqFinal) {
  const total = Math.floor((TAXA * duracaoMs) / 1000);
  const amostras = new Array(total);
  let fase = 0;
  for (let i = 0; i < total; i++) {
    const t = i / total;
    const f = freqFinal ? freq + (freqFinal - freq) * t : freq;
    fase += (2 * Math.PI * f) / TAXA;
    let v;
    if (forma === 'quadrada') v = Math.sin(fase) >= 0 ? 1 : -1;
    else if (forma === 'triangular') v = (2 / Math.PI) * Math.asin(Math.sin(fase));
    else v = Math.sin(fase);
    // envelope: ataque curto e decaimento suave, evita estalos
    const ataque = Math.min(1, i / (TAXA * 0.005));
    const decaimento = Math.pow(1 - t, 1.5);
    amostras[i] = v * volume * ataque * decaimento;
  }
  return amostras;
}

function silencio(ms) {
  return new Array(Math.floor((TAXA * ms) / 1000)).fill(0);
}

function salvar(nome, partes) {
  const dados = [].concat(...partes);
  const buffer = Buffer.alloc(44 + dados.length * 2);
  buffer.write('RIFF', 0);
  buffer.writeUInt32LE(36 + dados.length * 2, 4);
  buffer.write('WAVE', 8);
  buffer.write('fmt ', 12);
  buffer.writeUInt32LE(16, 16);
  buffer.writeUInt16LE(1, 20); // PCM
  buffer.writeUInt16LE(1, 22); // mono
  buffer.writeUInt32LE(TAXA, 24);
  buffer.writeUInt32LE(TAXA * 2, 28);
  buffer.writeUInt16LE(2, 32);
  buffer.writeUInt16LE(16, 34);
  buffer.write('data', 36);
  buffer.writeUInt32LE(dados.length * 2, 40);
  dados.forEach((v, i) => {
    const s = Math.max(-1, Math.min(1, v));
    buffer.writeInt16LE(Math.round(s * 32767), 44 + i * 2);
  });
  fs.mkdirSync(DESTINO, { recursive: true });
  fs.writeFileSync(path.join(DESTINO, nome), buffer);
  console.log('gerado', nome, (dados.length / TAXA).toFixed(2) + 's');
}

// Vitória: arpejo ascendente C5-E5-G5-C6
salvar('som_vitoria.wav', [
  nota(523.25, 110, 'quadrada', 0.35),
  nota(659.25, 110, 'quadrada', 0.35),
  nota(783.99, 110, 'quadrada', 0.35),
  nota(1046.5, 260, 'quadrada', 0.35),
]);

// Derrota: notas descendentes
salvar('som_derrota.wav', [
  nota(392.0, 150, 'triangular', 0.6),
  nota(329.63, 150, 'triangular', 0.6),
  nota(261.63, 150, 'triangular', 0.6),
  nota(196.0, 120, 'triangular', 0.6, 130.0),
  nota(130.0, 220, 'triangular', 0.6),
]);

// Empate: dois toques iguais
salvar('som_empate.wav', [
  nota(440.0, 110, 'seno', 0.6),
  silencio(60),
  nota(440.0, 160, 'seno', 0.6),
]);

// Sorteio: sequência rápida de bips e um "ding" final
const bipes = [330, 392, 349, 440, 392, 494, 440, 523].map((f) => nota(f, 55, 'quadrada', 0.3));
salvar('som_sorteio.wav', [...bipes, silencio(40), nota(1046.5, 300, 'seno', 0.6)]);
