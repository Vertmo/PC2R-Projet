(*******************************************************************************)
(*               Current state of the game, as known by the player             *)
(*******************************************************************************)


(** Phase: attente ou jeu *)
type phase = Attente | Jeu

(** Player state (coordinates, speed and score) *)
type playerState = {
  mutable username: string;
  mutable coord: float * float;
  mutable score: int;
}

(** State of the whole game *)
type gameState = {
  mutable coords: (string * (float * float)) list;
  mutable scores: (string * int) list;
  mutable phase: phase;
  mutable player: playerState;
}

let state = {
  coords = [];
  scores = [];
  phase = Attente;
  player = {
    username = "";
    coord = (0., 0.);
    score = 0;
  }
}

let stateMut = Mutex.create ()
